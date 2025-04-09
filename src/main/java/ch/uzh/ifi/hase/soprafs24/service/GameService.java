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

        if(newgame.getMaximalPlayers() < 2 || newgame.getMaximalPlayers() > 10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of players must be between 2 and 10");
        }
        if(newgame.getStartCredit() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starting credit must be greater than 0");
        }
        if(!newgame.getIsPublic() && newgame.getPassword() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private games must have a password");
        }
        if(newgame.getIsPublic() && newgame.getPassword() != null){
            newgame.setPassword(null);
        }
        if(newgame.getCreatorId() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator ID must not be null");
        }

        authenticator.checkTokenValidity(token);

        newgame = gameRepository.save(newgame);
        gameRepository.flush();

        return newgame;
        
    }

    public Game joinGame(Long gameId, String userToken, String password){

        //Check if the token is valid
        authenticator.checkTokenValidity(userToken);


        Game jointGame = gameRepository.findByid(gameId);
        if (jointGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        if(!(jointGame.getIsPublic())){
            if(!(Objects.equals(jointGame.getPassword(), password))){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password. Entry to game DENIED.");
            }
        }
        //Password checked OR its public
        User user = userRepository.findByToken(userToken);

        if (jointGame.getPlayers().size() >= jointGame.getMaximalPlayers()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is full. Entry to game DENIED.");
        }
        
        List<String> hand = new ArrayList<>();
        Player jointPlayer = new Player(user.getId(), hand, jointGame);
        jointGame.addPlayer(jointPlayer);

        return jointGame;
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

}