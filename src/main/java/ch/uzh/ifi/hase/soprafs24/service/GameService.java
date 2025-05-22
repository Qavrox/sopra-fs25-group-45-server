package ch.uzh.ifi.hase.soprafs24.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.helpers.OddsCalculator;
import ch.uzh.ifi.hase.soprafs24.helpers.PokerHelperPromptGenerator;
import ch.uzh.ifi.hase.soprafs24.helpers.SecretManagerHelper;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserStatisticsDTO;
import ch.uzh.ifi.hase.soprafs24.service.Authenticator;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final Authenticator authenticator;
    private final RestTemplate restTemplate;
    private final SecretManagerHelper secretManagerHelper;
    
    @Autowired
    private GameHistoryService gameHistoryService;
    
    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("userRepository") UserRepository userRepository, 
                       @Qualifier("playerRepository") PlayerRepository playerRepository,
                       GameHistoryService gameHistoryService,
                       SecretManagerHelper secretManagerHelper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.gameHistoryService = gameHistoryService;
        this.authenticator = new Authenticator(userRepository, gameRepository);
        this.restTemplate = new RestTemplate();
        this.secretManagerHelper = secretManagerHelper;
    }    

    
    public Game createNewGame(Game newgame, String token){
        // Validate the token and find the user
        authenticator.checkTokenValidity(token);
        User creator = userRepository.findByToken(token);
        if (creator == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found");
        }
        
        // Validate game creation parameters
        validateGameCreationParameters(newgame);
        
        // Verify creator ID matches token
        if (!creator.getId().equals(newgame.getCreatorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token does not match creator ID");
        }
        
        // Set initial game state and initialize the pot, callAmount
        initializeGameState(newgame);
        
        // Save the game to the database
        newgame = gameRepository.save(newgame);
        gameRepository.flush();

        return newgame;
    }

    /**
     * Validates all required parameters for game creation
     */
    private void validateGameCreationParameters(Game game) {
        if(game.getMaximalPlayers() < 2 || game.getMaximalPlayers() > 10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of players must be between 2 and 10");
        }
        if(game.getStartCredit() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starting credit must be greater than 0");
        }
        if(!game.getIsPublic() && (game.getPassword() == null || game.getPassword().isEmpty())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private games must have a password");
        }
        if(game.getIsPublic() && game.getPassword() != null){
            game.setPassword(null); // Remove password if the game is public
        }
        if(game.getCreatorId() == null || game.getCreatorId() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator ID must be provided");
        }
        if(game.getSmallBlind() <= 0 || game.getBigBlind() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blind values must be greater than 0");
        }
        if(game.getSmallBlind() >= game.getBigBlind()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Small blind must be less than big blind");
        }
    }
    
    /**
     * Initializes game state with default values
     */
    private void initializeGameState(Game game) {
        game.setPot(0L);
        game.setCallAmount(0L);
        game.setGameStatus(GameStatus.READY);
        game.setCommunityCards(new ArrayList<>());
    }

    public synchronized void joinGame(Long gameId, String userToken, String password){
        // Validate the token and find the user
        authenticator.checkTokenValidity(userToken);
        User user = userRepository.findByToken(userToken);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        
        // Find the game and validate it exists
        Game game = gameRepository.findByid(gameId);
        if (game == null || game.getStatus() == GameStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Validate game join conditions
        validateGameJoinConditions(game, user, password);
        
        // Add user as a player if not already in the game
        boolean wasAdded = addUserAsPlayer(user, game);
        
        // Make sure to save the game if user was added
        if (wasAdded) {
            gameRepository.save(game);
            gameRepository.flush();
        }
    }
    
    /**
     * Validates conditions for joining a game
     */
    private void validateGameJoinConditions(Game game, User user, String password) {
        // Check if user is already in the game
        for (Player player : game.getPlayers()) {
            if (player.getUserId().equals(user.getId())) {
                // Only return early if THIS user is already in the game
                return;  // This user is already in the game, allow them to "join" again
            }
        }
        
        // Check password for private games
        if(!game.getIsPublic()) {
            if(password == null || !Objects.equals(game.getPassword(), password)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password. Entry to game DENIED.");
            }
        }
        
        // Check if game is full
        if (game.getPlayers().size() >= game.getMaximalPlayers()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is full. Entry to game DENIED.");
        }
        
        // Check if game is in a joinable state
        if (game.getGameStatus() != null && game.getGameStatus() != GameStatus.READY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game has already started. New players cannot join.");
        }
    }
    
    /**
     * Adds a user as a player in the game if they're not already in it
     * @return true if the user was newly added, false if they were already in the game
     */
    private boolean addUserAsPlayer(User user, Game game) {
        // Check if user is already in the game
        for (Player player : game.getPlayers()) {
            if (player.getUserId().equals(user.getId())) {
                // User is already in the game, no need to add them again
                return false;
            }
        }
        
        // Add user as new player
        List<String> hand = new ArrayList<>();
        Player newPlayer = new Player(user.getId(), hand, game);
        game.addPlayer(newPlayer);
        
        // Save just the player
        playerRepository.save(newPlayer);
        playerRepository.flush();
        
        return true;
    }

    
    public List<Game> getAllPublicGames(String token) {
        // Check if the token is valid
        authenticator.checkTokenValidity(token);

        List<Game> allGames = gameRepository.findAll();
        List<Game> publicGames = new ArrayList<>();

        for (Game game : allGames) {
            if (game.getIsPublic() && game.getGameStatus() != GameStatus.ARCHIVED) {
                publicGames.add(game);
            }
        }
        return publicGames;
    }
    
    public Game getGameById(Long id, String authenticatorToken) {

        Game game = gameRepository.findByid(id);
        if (game == null || game.getStatus() == GameStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the token is the same as any of the players in the game (This is not the same as checkTokenValidity, but it indirectly checks the validity of the token)
        if(!(game.getIsPublic())){
            User user = userRepository.findByToken(authenticatorToken);
            List<Player> players = game.getPlayers();
        
            for (Player player : players) {
                if (player.getUserId().equals(user.getId())) {
                    return game;
                }
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is private. Entry to game DENIED, because Token does not match any player in game.");

        }
        return game;
    }


    public Game startRound(Long gameId, String token) {
        // Validate token and get game
        authenticator.checkTokenValidity(token);
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Reset game state
        game.setPot(0L);
        game.setCallAmount(0L);
        game.setGameStatus(GameStatus.READY);
        game.setCommunityCards(new ArrayList<>());
        game.setCurrentPlayerIndex(0);
        game.setLastRaisePlayerIndex(-1);

        game.rotateBlinds();

        // Reset player states while preserving credits
        for (Player player : game.getPlayers()) {
            player.setHand(new ArrayList<>());
            player.setCurrentBet(0L);
            player.setHasFolded(false);
            player.setHasActed(false);
            player.setLastAction(null);
            playerRepository.save(player);
        }

        // Save game state
        game = gameRepository.save(game);
        gameRepository.flush();

        return game;
    }
    
    public Game startPreFlop(Long gameId){
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        if (game.getGameStatus() != GameStatus.READY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is not in ready phase");
        }

        game.setGameStatus(GameStatus.PREFLOP);
        
        // Give players two cards 
        for (Player player : game.getPlayers()) {
            List<String> hand = new ArrayList<>();
            hand.add(game.getRandomCard());
            hand.add(game.getRandomCard());

            player.setHand(hand);
            playerRepository.save(player);
            playerRepository.flush();

        }
        gameRepository.save(game);
        gameRepository.flush();

        
        return game;
    }

    public Game placeCommunityCards(Long gameId){
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        if(!(game.getGameStatus() == GameStatus.PREFLOP || game.getGameStatus() == GameStatus.FLOP || game.getGameStatus() == GameStatus.TURN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is not in the right phase");
        }

        List <String> communityCards = game.getCommunityCards();
        communityCards.add(game.getRandomCard());

        if(game.getGameStatus() == GameStatus.PREFLOP){
            game.setGameStatus(GameStatus.FLOP);
            communityCards.add(game.getRandomCard());
            communityCards.add(game.getRandomCard());
        }
        else if(game.getGameStatus() == GameStatus.FLOP){
            game.setGameStatus(GameStatus.TURN);
        }
        else{
            game.setGameStatus(GameStatus.RIVER);
        }
        
        game.setCommunityCards(communityCards);
        gameRepository.save(game);
        gameRepository.flush();
        
        return game;
    }

    /**
     * Process a player action (check, call, bet, raise, fold)
     */
    public Game processPlayerAction(Long gameId, Long userId, PlayerAction action, Long amount) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Find the player
        Player player = null;
        int playerIndex = -1;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player p = game.getPlayers().get(i);
            if (p.getUserId().equals(userId)) {
                player = p;
                playerIndex = i;
                break;
            }
        }
        
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found in this game");
        }
        
        // Check if it's the player's turn
        if (playerIndex != game.getCurrentPlayerIndex()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "It's not your turn");
        }
        
        // Check if player has already folded
        if (player.getHasFolded()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have already folded");
        }
        
        // Get the highest current bet
        Long highestBet = 0L;
        for (Player p : game.getPlayers()) {
            if (p.getCurrentBet() > highestBet) {
                highestBet = p.getCurrentBet();
            }
        }
        
        // Process the action
        switch (action) {
            case CHECK:
                if (highestBet > player.getCurrentBet()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot check when there are bets");
                }
                player.setHasActed(true);
                player.setLastAction(PlayerAction.CHECK);
                break;
                
            case CALL:
                Long callAmount = highestBet - player.getCurrentBet();
                if (callAmount > player.getCredit()) {
                    callAmount = player.getCredit(); // All-in
                }
                player.setCredit(player.getCredit() - callAmount);
                player.setCurrentBet(player.getCurrentBet() + callAmount);
                player.setHasActed(true);
                player.setLastAction(PlayerAction.CALL);
                break;
                
            case BET:
                if (highestBet > 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot bet when there are already bets, use raise instead");
                }
                if (amount <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bet amount must be positive");
                }
                if (amount > player.getCredit()) {
                    amount = player.getCredit(); // All-in
                }
                player.setCredit(player.getCredit() - amount);
                player.setCurrentBet(amount);
                player.setHasActed(true);
                player.setLastAction(PlayerAction.BET);
                game.setLastRaisePlayerIndex(playerIndex);
                game.setCallAmount(amount);
                break;
                
            case RAISE:
                if (highestBet <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot raise when there are no bets, use bet instead");
                }
                if (amount <= highestBet) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Raise amount must be higher than current highest bet");
                }
                Long raiseAmount = amount - player.getCurrentBet();
                if (raiseAmount > player.getCredit()) {
                    amount = player.getCurrentBet() + player.getCredit(); // All-in
                }
                player.setCredit(player.getCredit() - (amount - player.getCurrentBet()));
                player.setCurrentBet(amount);
                player.setHasActed(true);
                player.setLastAction(PlayerAction.RAISE);
                game.setLastRaisePlayerIndex(playerIndex);
                game.setCallAmount(amount);
                break;
                
            case FOLD:
                player.setHasFolded(true);
                player.setHasActed(true);
                player.setLastAction(PlayerAction.FOLD);
                break;
                
            case ALL_IN:
                if (player.getCredit() <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no chips left to go all-in");
                }
                Long allInAmount = player.getCredit();
                Long newTotalBet = player.getCurrentBet() + allInAmount;
            
                player.setCurrentBet(newTotalBet);
                player.setCredit(0L);
                player.setHasActed(true);
                player.setLastAction(PlayerAction.ALL_IN);
            
                if (newTotalBet > highestBet) {
                    game.setCallAmount(newTotalBet);
                    game.setLastRaisePlayerIndex(playerIndex);
                }
                break;
                
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action");
        }
        
        // Save player state
        playerRepository.save(player);
        playerRepository.flush();
        
        // Check if betting round is complete
        if (game.isBettingRoundComplete()) {
            advanceGamePhase(game);
        } else {
            // Move to next player
            game.moveToNextPlayer();
        }
        
        // Save game state
        gameRepository.save(game);
        gameRepository.flush();
        
        return game;
    }
    
    /**
     * Advance the game to the next phase
     */
    private void advanceGamePhase(Game game) {
        // Collect bets into pot
        game.collectBetsIntoPot();
        
        // Check if only one player remains (everyone else folded)
        int activePlayers = 0;
        Player lastActivePlayer = null;
        for (Player player : game.getPlayers()) {
            if (!player.getHasFolded()) {
                activePlayers++;
                lastActivePlayer = player;
            }
        }
        
        if (activePlayers == 1) {
            // Record game history
            List<Player> winners = new ArrayList<>();
            winners.add(lastActivePlayer);
            game.setWinners(winners);
            lastActivePlayer.setCredit(lastActivePlayer.getCredit() + game.getPot());
            recordGameResults(game, winners);
            game.setGameStatus(GameStatus.GAMEOVER);
        }
        
        // Advance to next game phase
        switch (game.getGameStatus()) {
            case PREFLOP:
                // Move to flop - place 3 community cards
                List<String> communityCards = new ArrayList<>();
                System.err.println("Community cards1: " + communityCards);
                communityCards.add(game.getRandomCard());
                communityCards.add(game.getRandomCard());
                communityCards.add(game.getRandomCard());
                System.err.println("Community cards2: " + communityCards);
                game.setCommunityCards(communityCards);
                System.err.println("Community cards3: " + communityCards);
                game.setGameStatus(GameStatus.FLOP);
                break;
                
            case FLOP:
                // Move to turn - add 1 more community card
                List<String> flopCards = game.getCommunityCards();
                flopCards.add(game.getRandomCard());
                game.setCommunityCards(flopCards);
                game.setGameStatus(GameStatus.TURN);
                break;
                
            case TURN:
                // Move to river - add 1 more community card
                List<String> turnCards = game.getCommunityCards();
                turnCards.add(game.getRandomCard());
                game.setCommunityCards(turnCards);
                game.setGameStatus(GameStatus.RIVER);
                break;
                
            case RIVER:
                // Move to showdown
                game.setGameStatus(GameStatus.SHOWDOWN);
                
                // Determine winner and award pot
                determineWinnerAndAwardPot(game);
                endGameAndRecordHistory(game.getId());
                game.setGameStatus(GameStatus.GAMEOVER);
                
                // Record game history

                break;
                
            default:
                break;
        }
        
        // Reset player actions for the new betting round if game is not over
        if (game.getGameStatus() != GameStatus.GAMEOVER) {
            game.resetPlayerActions();
        }

        gameRepository.save(game);
        gameRepository.flush();
    }
    
    /**
     * Determine the winner and award the pot
     */
    private void determineWinnerAndAwardPot(Game game) {
        // Determine winners based on hand evaluation
        List<Player> winners = determineWinners(game.getId());
        
        // Calculate pot share per winner
        Long potPerWinner = game.getPot() / winners.size();
        
        // Award pot to winners
        for (Player winner : winners) {
            winner.setCredit(winner.getCredit() + potPerWinner);
        }

    }
    
    /**
     * Start a new betting round with blinds
     */
    public Game startBettingRound(Long gameId) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        if (game.getGameStatus() != GameStatus.READY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is not in ready phase");
        }
        
        // Set small and big blinds
        Player smallBlindPlayer = game.getPlayers().get(game.getSmallBlindIndex());
        Player bigBlindPlayer = game.getPlayers().get(game.getBigBlindIndex());
        
        // Small blind is typically half the big blind
        Long smallBlindAmount = 5L; // You might want to make this configurable
        Long bigBlindAmount = 10L;  // You might want to make this configurable
        
        // Place small blind
        if (smallBlindAmount > smallBlindPlayer.getCredit()) {
            smallBlindAmount = smallBlindPlayer.getCredit(); // All-in
        }
        smallBlindPlayer.setCredit(smallBlindPlayer.getCredit() - smallBlindAmount);
        smallBlindPlayer.setCurrentBet(smallBlindAmount);
        smallBlindPlayer.setHasActed(true);
        
        // Place big blind
        if (bigBlindAmount > bigBlindPlayer.getCredit()) {
            bigBlindAmount = bigBlindPlayer.getCredit(); // All-in
        }
        bigBlindPlayer.setCredit(bigBlindPlayer.getCredit() - bigBlindAmount);
        bigBlindPlayer.setCurrentBet(bigBlindAmount);
        bigBlindPlayer.setHasActed(true);
        
        // Set call amount to big blind
        game.setCallAmount(bigBlindAmount);
        
        // Set current player to the one after big blind
        game.setCurrentPlayerIndex((game.getBigBlindIndex() + 1) % game.getPlayers().size());
        
        // Set game status to preflop
        game.setGameStatus(GameStatus.PREFLOP);
        
        // Save player states
        playerRepository.save(smallBlindPlayer);
        playerRepository.save(bigBlindPlayer);
        playerRepository.flush();

        game.initializeShuffledDeck();

        // Remove cards from players (violently if needed)
        for (Player player : game.getPlayers()) {
            List<String> hand = new ArrayList<>();
            player.setHand(hand);
            playerRepository.save(player);
            playerRepository.flush();

        }

        // Give players two cards 
        for (Player player : game.getPlayers()) {
            List<String> hand = new ArrayList<>();
            hand.add(game.getRandomCard());
            hand.add(game.getRandomCard());

            player.setHand(hand);
            playerRepository.save(player);
            playerRepository.flush();

        }

        // Remove community cards
        game.setCommunityCards(new ArrayList<>());
        
        // Save game state
        gameRepository.save(game);
        gameRepository.flush();
        
        return game;
    }

    public List<Player> determineWinners(Long gameId) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        List<Player> players = game.getPlayers();
        List<String> communityCards = game.getCommunityCards();
        
        if (communityCards.size() < 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough community cards to determine winner");
        }

        // Convert community cards to Card objects
        List<Card> communityCardObjects = new ArrayList<>();
        for (String cardStr : communityCards) {
            communityCardObjects.add(Card.fromShortString(cardStr));
        }

        // Evaluate each player's hand
        List<Player> winners = new ArrayList<>();
        OddsCalculator.HandValue bestHandValue = null;

        for (Player player : players) {
            // Convert player's hand to Card objects
            List<Card> playerCards = new ArrayList<>();
            for (String cardStr : player.getHand()) {
                playerCards.add(Card.fromShortString(cardStr));
            }

            // Combine player's cards with community cards
            List<Card> allCards = new ArrayList<>(playerCards);
            allCards.addAll(communityCardObjects);

            // Evaluate the hand
            OddsCalculator.HandValue currentHandValue = OddsCalculator.evaluateHand(allCards);

            // Compare with best hand so far
            if (bestHandValue == null || currentHandValue.compareTo(bestHandValue) > 0) {
                // New best hand found
                winners.clear();
                winners.add(player);
                bestHandValue = currentHandValue;
            } else if (currentHandValue.compareTo(bestHandValue) == 0) {
                // Tied with best hand
                winners.add(player);
            }
        }

        game.setWinners(winners);

        return winners;
    }

    public double calculateWinProbability(Long gameId, Long userId) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Find the player
        Player player = null;
        for (Player p : game.getPlayers()) {
            if (p.getUserId().equals(userId)) {
                player = p;
                break;
            }
        }

        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found in game");
        }

        // Get the player's hand and community cards
        List<String> playerHand = player.getHand();
        List<String> communityCards = game.getCommunityCards();

        // Convert cards to Card objects
        List<Card> playerCards = new ArrayList<>();
        for (String cardStr : playerHand) {
            playerCards.add(Card.fromShortString(cardStr));
        }

        List<Card> communityCardObjects = new ArrayList<>();
        for (String cardStr : communityCards) {
            communityCardObjects.add(Card.fromShortString(cardStr));
        }

        // Calculate win probability using OddsCalculator
        return OddsCalculator.calculateWinProbability(playerCards, communityCardObjects, game.getPlayers().size());
    }

    public Game deleteGame(Long gameId, String token){
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the token is the same as the creator of the game in case the token is not empty
        long gameCreatorId = game.getCreatorId();

        User gameCreator = userRepository.findByid(gameCreatorId);

        if(!(token.isEmpty()) && !gameCreator.getToken().equals(token)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of the game. You cannot delete the game.");
        }

        // clear players
        game.getPlayers().clear();

        // Archieve the the game
        game.setStatus(GameStatus.ARCHIVED);
        gameRepository.save(game);
        gameRepository.flush();
        return game;
    }

    public synchronized void leaveGame(Long gameId, String userToken) {

        // authenticate & resolve user
        authenticator.checkTokenValidity(userToken);
        User user = userRepository.findByToken(userToken);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // load game
        Game game = gameRepository.findByid(gameId);
        if (game == null || game.getStatus() == GameStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        if (game.getStatus() != GameStatus.GAMEOVER){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to leave game at this phase"); //New condition added 
        }

        // locate the player instance that belongs to this user
        Player playerToRemove = null;
        for (Player p : game.getPlayers()) {
            if (p.getUserId().equals(user.getId())) {
                playerToRemove = p;
                break;
            }
        }
        if (playerToRemove == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not part of this game");
        }

        // remove player from game & DB
        game.removePlayer(playerToRemove);
        

        // persist updated game state
        gameRepository.save(game);
        gameRepository.flush();
    }

    public String getHandDescription(Player player, List<String> communityCards) {
        // Convert player's hand to Card objects
        List<Card> playerCards = new ArrayList<>();
        for (String cardStr : player.getHand()) {
            playerCards.add(Card.fromShortString(cardStr));
        }

        // Convert community cards to Card objects
        List<Card> communityCardObjects = new ArrayList<>();
        for (String cardStr : communityCards) {
            communityCardObjects.add(Card.fromShortString(cardStr));
        }

        // Combine all cards
        List<Card> allCards = new ArrayList<>(playerCards);
        allCards.addAll(communityCardObjects);

        // Evaluate the hand
        OddsCalculator.HandValue handValue = OddsCalculator.evaluateHand(allCards);
        
        // Return hand description
        return handValue.toString();
    }

    /**
     * Get poker advice from Gemini AI for the specified player in the game
     * 
     * @param gameId The ID of the game
     * @param userId The ID of the user requesting advice
     * @return AI-generated poker advice or an error message if the API call fails
     */
    public String getPokerAdvice(Long gameId, Long userId) {
        // Get API key from SecretManagerHelper
        String geminiApiKey = secretManagerHelper.getGeminiApiKey();
        
        // Get the game state
        Game game = gameRepository.findByid(gameId);
        if (game == null || game.getStatus() == GameStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Find the player in the game
        Player player = null;
        for (Player p : game.getPlayers()) {
            if (p.getUserId().equals(userId)) {
                player = p;
                break;
            }
        }
        
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Player not found in this game");
        }
        
        // Get the game state information
        List<String> communityCards = game.getCommunityCards();
        List<String> playerHand = player.getHand();
        Long potSize = game.getPot();
        Long playerCredit = player.getCredit();
        GameStatus gameStatus = game.getGameStatus();
        
        // Calculate win probability
        double winProbability = calculateWinProbability(gameId, userId);
        
        // Generate the prompt for Gemini
        String prompt = PokerHelperPromptGenerator.generatePrompt(
            communityCards,
            playerHand,
            potSize,
            playerCredit,
            gameStatus,
            (float) winProbability
        );
        
        // Prepare the request body for Gemini API
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        List<Map<String, Object>> parts = new ArrayList<>();
        
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);
        
        content.put("parts", parts);
        contents.add(content);
        
        requestBody.put("contents", contents);
        
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // Make the API call
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent?key=" + geminiApiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            // Extract and return the response text
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // Check for error in response
                if (responseBody.containsKey("error")) {
                    Map<String, Object> error = (Map<String, Object>) responseBody.get("error");
                    String errorMessage = error.containsKey("message") 
                        ? (String) error.get("message") 
                        : "Unknown error from Gemini API";
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, 
                        "Gemini API error: " + errorMessage);
                }
                
                // Parse the nested JSON structure to extract only the text content
                if (!responseBody.containsKey("candidates")) {
                    return "Unable to get poker advice at this time - no candidates in response.";
                }
                
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (candidates == null || candidates.isEmpty()) {
                    return "Unable to get poker advice at this time - empty candidates list.";
                }
                
                Map<String, Object> candidate = candidates.get(0);
                if (!candidate.containsKey("content")) {
                    return "Unable to get poker advice at this time - no content in candidate.";
                }
                
                Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
                if (!candidateContent.containsKey("parts")) {
                    return "Unable to get poker advice at this time - no parts in content.";
                }
                
                List<Map<String, Object>> contentParts = (List<Map<String, Object>>) candidateContent.get("parts");
                if (contentParts == null || contentParts.isEmpty()) {
                    return "Unable to get poker advice at this time - empty parts list.";
                }
                
                Map<String, Object> part = contentParts.get(0);
                if (!part.containsKey("text")) {
                    return "Unable to get poker advice at this time - no text in part.";
                }
                
                return (String) part.get("text");
            }
            
            return "Unable to get poker advice at this time - unexpected response status: " + response.getStatusCode();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error calling Gemini API: " + e.getMessage());
        }
    }


    /**
     * End a game and record game history for all players
     * This method should be called when a game is completed
     * @param gameId - ID of the game to end
     */
    public void endGameAndRecordHistory(Long gameId) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Determine winners and record game results
        List<Player> winners = determineWinners(gameId);
        recordGameResults(game, winners);
        
        // Update game status to completed
        //game.setGameStatus(GameStatus.ARCHIVED);
        gameRepository.save(game);
        gameRepository.flush();
    }
    
    /**
     * Records the game results for all players
     */
    private void recordGameResults(Game game, List<Player> winners) {
        // Get all player IDs except the current player
        List<Long> allPlayerIds = game.getPlayers().stream()
                .map(Player::getUserId)
                .collect(Collectors.toList());
        
        System.out.println("players: " + allPlayerIds);
        // Calculate winnings for each player
        Long potPerWinner = game.getPot() / winners.size();
        
        // Record results for all players
        for (Player player : game.getPlayers()) {
            // Create a list of other player IDs
            List<Long> otherPlayerIds = new ArrayList<>(allPlayerIds);
            otherPlayerIds.remove(player.getUserId());
            
            // Determine if this player is a winner
            boolean isWinner = winners.stream()
                    .anyMatch(w -> w.getUserId().equals(player.getUserId()));
            
            // Calculate winnings - winners get their share of the pot, losers get negative their bet amount
            Long winnings = 0L;
            if (isWinner) {
                winnings = potPerWinner - player.getTotalBets();
            } else {
                // For losers, winnings is negative (they lost their bet)
                winnings = - player.getTotalBets();
            }

            System.out.println("winnings: " + winnings);
            
            // Record the game result
            gameHistoryService.recordGameResult(
                    player.getUserId(),
                    game.getId(),
                    isWinner ? "Win" : "Loss",
                    winnings,
                    otherPlayerIds
            );
        }
    }
    
    /**
     * Get game history for a user
     * @param userId - ID of the user
     * @param token - authentication token
     * @return list of game history records
     */
    public List<GameHistory> getUserGameHistory(Long userId, String token) {
        // Validate the token and find the user
        authenticator.checkTokenValidity(token);
        User user = userRepository.findByToken(token);
        
        // Check if the requesting user is the same as the target user
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own game history");
        }
        
        // Get game history from the service
        return gameHistoryService.getUserGameHistory(userId);
    }
    
    /**
     * Get user statistics
     * @param userId - ID of the user
     * @param token - authentication token
     * @return user statistics
     */
    public UserStatisticsDTO getUserStatistics(Long userId, String token) {
        // Validate the token and find the user
        authenticator.checkTokenValidity(token);
        
        // Get statistics from the service
        return gameHistoryService.getUserStatistics(userId,null,null);
    }
    
    /**
     * Get leaderboard by winnings
     * @param token - authentication token
     * @return leaderboard sorted by total winnings
     */
    public List<LeaderboardEntryDTO> getLeaderboardByWinnings(String token) {
        // Validate the token
        authenticator.checkTokenValidity(token);
        
        // Get leaderboard from the service
        return gameHistoryService.getLeaderboardByWinnings();
    }
    
    /**
     * Get leaderboard by win rate
     * @param token - authentication token
     * @return leaderboard sorted by win rate
     */
    public List<LeaderboardEntryDTO> getLeaderboardByWinRate(String token) {
        // Validate the token
        authenticator.checkTokenValidity(token);
        
        // Get leaderboard from the service
        return gameHistoryService.getLeaderboardByWinRate();
    }
}