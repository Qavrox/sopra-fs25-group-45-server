package ch.uzh.ifi.hase.soprafs24.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.helpers.OddsCalculator;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.service.Authenticator;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final Authenticator authenticator;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.authenticator = new Authenticator(userRepository, gameRepository);
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
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Validate game join conditions
        validateGameJoinConditions(game, user, password);
        
        // Add user as a player if not already in the game
        addUserAsPlayer(user, game);
    }
    
    /**
     * Validates conditions for joining a game
     */
    private void validateGameJoinConditions(Game game, User user, String password) {
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
     */
    private void addUserAsPlayer(User user, Game game) {
        // Check if user is already in the game
        for (Player player : game.getPlayers()) {
            if (player.getUserId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already in the game");
            }
        }
        
        // Add user as new player
        List<String> hand = new ArrayList<>();
        Player newPlayer = new Player(user.getId(), hand, game);
        game.addPlayer(newPlayer);
        
        // Save the updated game
        gameRepository.save(game);
        gameRepository.flush();
    }

    
    public List<Game> getAllPublicGames(String token) {
        // Check if the token is valid
        authenticator.checkTokenValidity(token);

        List<Game> allGames = gameRepository.findAll();
        List<Game> publicGames = new ArrayList<>();

        for (Game game : allGames) {
            if (game.getIsPublic()) {
                publicGames.add(game);
            }
        }
        return publicGames;
    }
    
    public Game getGameById(Long id, String authenticatorToken) {

        Game game = gameRepository.findByid(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the token is the same as any of the players in the game (This is not the same as checkTokenValidity, but it indirectly checks the validity of the token)
        if(!(game.getIsPublic())){
            User user = userRepository.findByToken(authenticatorToken);
            List<Player> players = game.getPlayers();
        
            for (Player player : players) {
                if (player.getUserId() == user.getId()) {
                    return game;
                }
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is private. Entry to game DENIED, because Token does not match any player in game.");

        }
        return game;
    }


    public Game startRound(Long gameId, String token){
        Game game = gameRepository.findByid(gameId);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }        

        //this is a mess... but basically, we're just checking that the creator of the game is the one who is trying to start the game
        Long gameCreatorId = game.getCreatorId();
        User gameCreator = userRepository.findByid(gameCreatorId);

        User user = userRepository.findByToken(token);
        if(gameCreator.getToken()!=user.getToken()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of the game. You cannot start the game.");
        }

        if (game.getPlayers().size() < 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough players to start the game");
        }
        
        game.setGameStatus(GameStatus.READY);
        game.setStartBlinds();
        List<String> newDeck = new ArrayList<>();
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String rank : ranks) {
                newDeck.add(rank + "" + suit);
            }
        }
        
        game.setCardDeck(newDeck);

        // Remove cards from players (violently if needed)
        for (Player player : game.getPlayers()) {
            List<String> hand = new ArrayList<>();
            player.setHand(hand);
            playerRepository.save(player);
            playerRepository.flush();

        }

        // Remove community cards
        game.setCommunityCards(new ArrayList<>());
        
        gameRepository.save(game);
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
    public Game processPlayerAction(Long gameId, Long playerId, PlayerAction action, Long amount) {
        Game game = gameRepository.findByid(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        
        // Find the player
        Player player = null;
        int playerIndex = -1;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player p = game.getPlayers().get(i);
            if (p.getId().equals(playerId)) {
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
            // Award pot to last player standing
            lastActivePlayer.setCredit(lastActivePlayer.getCredit() + game.getPot());
            game.setPot(0L);
            game.setGameStatus(GameStatus.GAMEOVER);
            return;
        }
        
        // Advance to next game phase
        switch (game.getGameStatus()) {
            case PREFLOP:
                // Move to flop - place 3 community cards
                List<String> communityCards = new ArrayList<>();
                communityCards.add(game.getRandomCard());
                communityCards.add(game.getRandomCard());
                communityCards.add(game.getRandomCard());
                game.setCommunityCards(communityCards);
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
                gameRepository.save(game);
                gameRepository.flush();
                
                // Determine winner and award pot
                determineWinnerAndAwardPot(game);
                game.setGameStatus(GameStatus.GAMEOVER);
                break;
                
            default:
                break;
        }
        
        // Reset player actions for the new betting round if game is not over
        if (game.getGameStatus() != GameStatus.GAMEOVER) {
            game.resetPlayerActions();
        }
    }
    
    /**
     * Determine the winner and award the pot
     * This is a placeholder - you would need to implement poker hand evaluation logic
     */
    private void determineWinnerAndAwardPot(Game game) {
        // For now, just award the pot to the first active player
        // In a real implementation, you would evaluate poker hands
        for (Player player : game.getPlayers()) {
            if (!player.getHasFolded()) {
                player.setCredit(player.getCredit() + game.getPot());
                game.setPot(0L);
                break;
            }
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

}
