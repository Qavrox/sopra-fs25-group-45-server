package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private Authenticator authenticator;

    @InjectMocks
    private GameService gameService;

    

    private Game game;
    private Game privateGame;
    private List<Player> players;
    private User user;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up user
        user= new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(java.time.LocalDate.now());
        user.setToken("valid-token");
        
        // Setup a basic game
        game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.RIVER);
        game.setMaximalPlayers(5);
        game.setStartCredit(1000L);
        game.setCreatorId(1);
        game.setIsPublic(true);
        game.setSmallBlind(1);
        game.setBigBlind(1);
        game.setSmallBlindIndex(0);
        game.setPot(1L);
        game.setCallAmount(1L);
        
        // Set up game with a private game
        privateGame = new Game();
        privateGame.setId(3L);
        privateGame.setGameStatus(GameStatus.RIVER);
        privateGame.setMaximalPlayers(5);
        privateGame.setStartCredit(1000L);
        privateGame.setCreatorId(1);
        privateGame.setIsPublic(false);
        privateGame.setPassword("password");
        privateGame.setSmallBlind(1);
        privateGame.setBigBlind(1);
        privateGame.setSmallBlindIndex(0);
        privateGame.setPot(1L);
        privateGame.setCallAmount(1L);
        
        // Initialize cardDeck and communityCards
        game.setCardDeck(new ArrayList<>());
        game.setCommunityCards(new ArrayList<>());
        
        // Add players
        players = new ArrayList<>();
        Player player1 = new Player(1L, new ArrayList<>(), game);
        Player player2 = new Player(2L, new ArrayList<>(), game);
        Player player3 = new Player(3L, new ArrayList<>(), game);
        
        players.add(player1);
        players.add(player2);
        players.add(player3);
        
        // Add players to game
        for (Player player : players) {
            game.addPlayer(player);
        }
        
        // Setup mocks
        when(gameRepository.findByid(1L)).thenReturn(game);
        when(gameRepository.findByid(3L)).thenReturn(privateGame);
        when(gameRepository.findByid(2L)).thenReturn(null);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.findAll()).thenReturn(Collections.singletonList(game));
      
        // Mock the authenticator
        Mockito.doNothing().when(authenticator).checkTokenValidity(any(String.class));
    }

    @Test
    void testDetermineWinners_NotEnoughCommunityCards() {
        // Setup with only 4 community cards
        List<String> communityCards = Arrays.asList("AH", "KH", "QH", "JH");
        game.setCommunityCards(communityCards);
        
        // Verify that an exception is thrown when there are not enough community cards
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.determineWinners(1L);
        });
        
        assertTrue(exception.getMessage().contains("Not enough community cards"));
    }

    @Test
    void testDetermineWinners_GameNotFound() {
        // Verify that an exception is thrown when the game is not found
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.determineWinners(2L);
        });
        
        assertTrue(exception.getMessage().contains("Game not found"));
    }
    
    @Test
    void testDetermineWinners_RoyalFlushWins() {
        // Setup community cards for a royal flush scenario
        List<String> communityCards = Arrays.asList("10S", "JS", "QS", "KS", "5H");
        game.setCommunityCards(communityCards);
        
        // Setup player hands
        players.get(0).setHand(Arrays.asList("AS", "2H")); // Royal flush (A-K-Q-J-10 of Spades)
        players.get(1).setHand(Arrays.asList("AH", "AD")); // Pair of Aces
        players.get(2).setHand(Arrays.asList("2S", "2D")); // Pair of 2s
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that only the player with the royal flush wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }
    
    @Test
    void testDetermineWinners_FullHouseBeatsFlush() {
        // Setup community cards
        List<String> communityCards = Arrays.asList("2S", "2C", "2D", "KH", "QH");
        game.setCommunityCards(communityCards);
        
        // Setup player hands - create clearly distinguishable hands
        players.get(0).setHand(Arrays.asList("KC", "KD")); // Full house: three 2s and a pair of Kings
        players.get(1).setHand(Arrays.asList("AH", "JH")); // Flush in hearts (A, K, Q, J hearts + one more)
        players.get(2).setHand(Arrays.asList("3H", "4H")); // Flush in hearts (lower than player 1)
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that only player with full house wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }
    
    @Test
    void testDetermineWinners_TieBreakerWithKicker() {
        // Setup community cards
        List<String> communityCards = Arrays.asList("AH", "AD", "3H", "4D", "5H");
        game.setCommunityCards(communityCards);
        
        // Setup player hands with the same pair but different kickers
        players.get(0).setHand(Arrays.asList("KH", "QH")); // Pair of Aces with K,Q kickers
        players.get(1).setHand(Arrays.asList("KD", "JD")); // Pair of Aces with K,J kickers
        players.get(2).setHand(Arrays.asList("QD", "JH")); // Pair of Aces with Q,J kickers
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that player with the highest kicker wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }



    @Test 
    void testCreateNewGameValidToken(){
        // when
        gameService.createNewGame(game, user.getToken());

        // then
        Game persistedGame = gameRepository.findByid(game.getId());
        assertNotNull(persistedGame);
        assert(Objects.equals(game, persistedGame));

    }

    @Test 
    void testCreateNewGameInvalidToken(){
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(game, "invalid token");
        });

    }

    @Test
    void testCreateGameInvalidMaximalPlayers() {
        // when
        Game gameWithTooFewPlayers = new Game();
        gameWithTooFewPlayers.setMaximalPlayers(0); // Invalid number of players

        Game gameWithTooManyPlayers = new Game();
        gameWithTooManyPlayers.setMaximalPlayers(11); // Invalid number of players


        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithTooFewPlayers, user.getToken());
        });

        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithTooManyPlayers, user.getToken());
        });
    }

    @Test
    void testCreateGameInvalidStartCredit() {
        // when
        Game gameWithNegativeStartCredit = new Game();
        gameWithNegativeStartCredit.setStartCredit(-100L); // Invalid start credit

        Game gameWithZeroStartCredit = new Game();
        gameWithZeroStartCredit.setStartCredit(0L); // Invalid start credit

        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithNegativeStartCredit, user.getToken());
        });

        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithZeroStartCredit, user.getToken());
        });
    }

    @Test 
    void testJoinGameValidToken(){
        // when
        Game jointGame = gameService.joinGame(game.getId(), user.getToken(), game.getPassword());

        assert(Objects.equals(game, jointGame));

    }

    @Test 
    void testJoinGameInvalidToken(){
        assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(game.getId(), "invalid token", game.getPassword());
        });

    }

    @Test 
    void testJoinGameWrongPassword(){

        assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(privateGame.getId(), user.getToken(), "wrong password");
        });
    }


    @Test
    void testgetAllPublicGamesValidToken() {

        List<Game> games = new ArrayList<>();
        games.add(game);
        // when
        List<Game> publicGames = gameService.getAllPublicGames(user.getToken());
        

        // then
        assertNotNull(publicGames);
        assertEquals(games, publicGames);
    }

    void testgetAllPublicGamesInvalidToken(){
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getAllPublicGames("invalid token");
        });
    }

    @Test
    void testGetGameByIdValidToken() {
        // when
        Game foundGame = gameService.getGameById(game.getId(), user.getToken());

        // then
        assertNotNull(foundGame);
        assertEquals(game, foundGame);
    }

    @Test
    void testGetGameByIdInvalidToken() {
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getGameById(privateGame.getId(), "invalid token");
        });
    }

    @Test
    void testGetGameByIdGameNotFound() {
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getGameById(2L, user.getToken());
        });
    }

}