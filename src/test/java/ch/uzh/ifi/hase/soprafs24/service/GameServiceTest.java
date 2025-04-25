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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

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
        user = new User();
        user.setId(1L);
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
        game.setCreatorId(1L);
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
        privateGame.setCreatorId(1L);
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
        when(userRepository.findByid(1L)).thenReturn(user);
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
        // Setup
        Game newGame = new Game();
        newGame.setCreatorId(user.getId());
        newGame.setIsPublic(true);
        newGame.setMaximalPlayers(5);
        newGame.setStartCredit(1000L);
        newGame.setSmallBlind(5);
        newGame.setBigBlind(10);
        newGame.setSmallBlindIndex(0);
        newGame.setPot(0L);
        newGame.setCallAmount(0L);
        newGame.setGameStatus(GameStatus.READY);
        newGame.setCardDeck(new ArrayList<>());
        newGame.setCommunityCards(new ArrayList<>());
        newGame.setPlayers(new ArrayList<>());

        // Mock repository behavior
        when(gameRepository.save(any(Game.class))).thenReturn(newGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);

        // Execute
        Game createdGame = gameService.createNewGame(newGame, user.getToken());

        // Verify
        assertNotNull(createdGame, "Created game should not be null");
        // Creator is no longer automatically added as a player
        assertEquals(0, createdGame.getPlayers().size(), "Game should not have any players yet");

        // Verify repository interactions
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).flush();
    }

    @Test 
    void testCreateNewGameInvalidToken(){
        // Setup
        Game newGame = new Game();
        newGame.setCreatorId(user.getId());
        newGame.setIsPublic(true);
        newGame.setMaximalPlayers(5);
        newGame.setStartCredit(1000L);

        // Mock authenticator to throw exception
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"))
               .when(authenticator).checkTokenValidity("invalid token");

        // Execute and verify
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(newGame, "invalid token");
        }, "Should throw exception for invalid token");

        // Verify repository interactions
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).flush();
    }

    @Test
    void testCreateGameInvalidMaximalPlayers() {
        // Setup
        Game gameWithTooFewPlayers = new Game();
        gameWithTooFewPlayers.setCreatorId(user.getId());
        gameWithTooFewPlayers.setMaximalPlayers(0); // Invalid number of players

        Game gameWithTooManyPlayers = new Game();
        gameWithTooManyPlayers.setCreatorId(user.getId());
        gameWithTooManyPlayers.setMaximalPlayers(11); // Invalid number of players

        // Mock user repository
        when(userRepository.findByToken(user.getToken())).thenReturn(user);

        // Execute and verify
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithTooFewPlayers, user.getToken());
        }, "Should throw exception for too few players");

        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithTooManyPlayers, user.getToken());
        }, "Should throw exception for too many players");

        // Verify repository interactions
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).flush();
    }

    @Test
    void testCreateGameInvalidStartCredit() {
        // Setup
        Game gameWithNegativeStartCredit = new Game();
        gameWithNegativeStartCredit.setCreatorId(user.getId());
        gameWithNegativeStartCredit.setStartCredit(-100L); // Invalid start credit

        Game gameWithZeroStartCredit = new Game();
        gameWithZeroStartCredit.setCreatorId(user.getId());
        gameWithZeroStartCredit.setStartCredit(0L); // Invalid start credit

        // Mock user repository
        when(userRepository.findByToken(user.getToken())).thenReturn(user);

        // Execute and verify
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithNegativeStartCredit, user.getToken());
        }, "Should throw exception for negative start credit");

        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithZeroStartCredit, user.getToken());
        }, "Should throw exception for zero start credit");

        // Verify repository interactions
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).flush();
    }

    @Test
    void testCreateNewGameCreatorNotFound() {
        // Setup
        Game newGame = new Game();
        newGame.setCreatorId(user.getId());
        newGame.setIsPublic(true);
        newGame.setMaximalPlayers(5);
        newGame.setStartCredit(1000L);

        // Mock repository to return null for creator
        when(userRepository.findByToken(user.getToken())).thenReturn(null);

        // Execute and verify
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(newGame, user.getToken());
        }, "Should throw exception when creator not found");

        // Verify repository interactions
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).flush();
    }

    @Test
    void testCreateNewGameTokenMismatch() {
        // Setup
        Game newGame = new Game();
        newGame.setCreatorId(999L); // Different from user.getId()
        newGame.setIsPublic(true);
        newGame.setMaximalPlayers(5);
        newGame.setStartCredit(1000L);

        // Mock repository behavior
        when(userRepository.findByToken(user.getToken())).thenReturn(user);

        // Execute and verify
        assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(newGame, user.getToken());
        }, "Should throw exception when token doesn't match creator ID");

        // Verify repository interactions
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).flush();
    }


    @Test 
    void testJoinGameValidToken(){
        // Create a fresh game for this test
        Game freshGame = new Game();
        freshGame.setId(1L);
        freshGame.setCreatorId(2L); // Different from user.getId()
        freshGame.setIsPublic(true);
        freshGame.setMaximalPlayers(5);
        freshGame.setStartCredit(1000L);
        freshGame.setSmallBlind(5);
        freshGame.setBigBlind(10);
        freshGame.setGameStatus(GameStatus.READY);
        freshGame.setPlayers(new ArrayList<>()); // Empty player list
        
        // Mock the repositories
        when(gameRepository.findByid(1L)).thenReturn(freshGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(freshGame);
        
        // Execute
        gameService.joinGame(1L, user.getToken(), null);
        
        // Verify
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).flush();
        
        // Verify that a player was added to the game
        assertEquals(1, freshGame.getPlayers().size(), "Game should have one player added");
        assertEquals(user.getId(), freshGame.getPlayers().get(0).getUserId(), "The player should have the user's ID");
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

    @Test
    void testStartRound_ValidGame() {
        // when
        Game updatedGame = gameService.startRound(game.getId(), user.getToken());
        // then
        assertNotNull(updatedGame);
        assertEquals(GameStatus.READY, updatedGame.getGameStatus());
        assertEquals(0, updatedGame.getSmallBlindIndex());;
        assertEquals(52, updatedGame.getCardDeck().size()); // Full deck of cards
        assertTrue(updatedGame.getCommunityCards().isEmpty()); // Community cards should be empty
        // Verify that all players' hands are cleared
        for (Player player : updatedGame.getPlayers()) {
            assertTrue(player.getHand().isEmpty());
        }
    }
   
    @Test
    void testStartRoundGameNotFound() {
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.startRound(2L, user.getToken()); // Game with ID 2 does not exist
        });
    }
    
    @Test
    void testStartRoundNotEnoughPlayers() {
        // Setup a game with only one player
        Game gameWithOnePlayer = new Game();
        gameWithOnePlayer.setId(4L);
        gameWithOnePlayer.setMaximalPlayers(5);
        gameWithOnePlayer.setStartCredit(1000L);
        gameWithOnePlayer.setCreatorId(1L);
        gameWithOnePlayer.setIsPublic(true);
        gameWithOnePlayer.addPlayer(new Player(1L, new ArrayList<>(), gameWithOnePlayer));
        when(gameRepository.findByid(4L)).thenReturn(gameWithOnePlayer);
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.startRound(4L, user.getToken());
        });
    }

    @Test
    void testStartRound_InvalidToken() {

        User notRoomCreator = new User();
        notRoomCreator.setName("Not Room Creator");
        notRoomCreator.setUsername("notroomcreator@lastname");
        notRoomCreator.setStatus(UserStatus.ONLINE);
        notRoomCreator.setCreationDate(java.time.LocalDate.now());
        notRoomCreator.setToken("invalid-token");
        // Mock the authenticator to throw an exception for invalid tokens
        Mockito.doNothing().when(authenticator).checkTokenValidity("invalid-token");
        when(userRepository.findByToken("invalid-token")).thenReturn(notRoomCreator);
        // when
        assertThrows(ResponseStatusException.class, () -> {
            gameService.startRound(game.getId(), "invalid-token");
        });
    }
    
    @Test
    void testCreatorJoiningOwnGame() {
        // Setup game for this specific test
        Game testGame = new Game();
        testGame.setId(1L);
        testGame.setCreatorId(user.getId());
        testGame.setIsPublic(true);
        testGame.setMaximalPlayers(5);
        testGame.setStartCredit(1000L);
        testGame.setSmallBlind(5);
        testGame.setBigBlind(10);
        testGame.setSmallBlindIndex(0);
        testGame.setPot(0L);
        testGame.setCallAmount(0L);
        testGame.setGameStatus(GameStatus.READY);
        testGame.setCardDeck(new ArrayList<>());
        testGame.setCommunityCards(new ArrayList<>());
        testGame.setPlayers(new ArrayList<>());
        
        // Mock the repository to return the same game instance
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(gameRepository.findByid(1L)).thenReturn(testGame);
        
        // First create the game (creator is not automatically added now)
        Game createdGame = gameService.createNewGame(testGame, user.getToken());
        assertNotNull(createdGame, "Created game should not be null");
        assertEquals(0, createdGame.getPlayers().size(), "Game should have no players yet");
        
        // Then have the creator join the game
        gameService.joinGame(1L, user.getToken(), null);
        
        // Verify that there is now one player with the creator's userId
        List<Player> players = testGame.getPlayers();
        assertEquals(1, players.size(), "There should be exactly one player");
        
        Player firstPlayer = players.get(0);
        assertNotNull(firstPlayer, "First player should not be null");
        assertEquals(user.getId(), firstPlayer.getUserId(), "The player should have the creator's userId");
        
        // Verify repository interactions
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(gameRepository, atLeastOnce()).findByid(1L);
    }

    @Test
    void testCreateNewGameWithCreatorAsPlayer() {
        // Create a new game
        Game newGame = new Game();
        newGame.setId(7L);
        newGame.setCreatorId(user.getId());
        newGame.setIsPublic(true);
        newGame.setMaximalPlayers(5);
        newGame.setStartCredit(1000L);
        newGame.setSmallBlind(5);
        newGame.setBigBlind(10);
        newGame.setPlayers(new ArrayList<>());
        
        // Mock repository behavior
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(newGame);
        
        // Create the game
        Game createdGame = gameService.createNewGame(newGame, user.getToken());
        
        // Verify that the creator is NOT added as a player
        assertEquals(0, createdGame.getPlayers().size(), "Game should not have any players yet");
        
        // Now manually join the creator to the game
        when(gameRepository.findByid(7L)).thenReturn(createdGame);
        gameService.joinGame(7L, user.getToken(), null);
        
        // Verify creator has now joined as a player
        assertEquals(1, createdGame.getPlayers().size(), "Game should have one player after joining");
        assertEquals(user.getId(), createdGame.getPlayers().get(0).getUserId(), "The player should be the creator");
    }

    @Test
    void testCreateGameValidParametersSuccess() {
        // Setup
        Game validGame = new Game();
        validGame.setCreatorId(user.getId());
        validGame.setIsPublic(true);
        validGame.setMaximalPlayers(5);
        validGame.setStartCredit(1000L);
        validGame.setSmallBlind(5);
        validGame.setBigBlind(10);
        validGame.setPlayers(new ArrayList<>());
        
        // Mock repository to return the saved game
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(validGame);
        
        // Execute
        Game createdGame = gameService.createNewGame(validGame, user.getToken());
        
        // Verify
        assertNotNull(createdGame);
        assertEquals(GameStatus.READY, createdGame.getGameStatus());
        assertEquals(0L, createdGame.getPot());
        assertEquals(0L, createdGame.getCallAmount());
        assertEquals(0, createdGame.getPlayers().size(), "Game should not have any players yet");
        verify(gameRepository).save(any(Game.class));
        verify(gameRepository).flush();
    }
    
    @Test
    void testCreateGamePrivateWithPassword() {
        // Setup
        Game privateGame = new Game();
        privateGame.setCreatorId(user.getId());
        privateGame.setIsPublic(false);
        privateGame.setMaximalPlayers(5);
        privateGame.setStartCredit(1000L);
        privateGame.setPassword("secret123");
        privateGame.setSmallBlind(5);
        privateGame.setBigBlind(10);
        
        // Mock repository to return the saved game
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(privateGame);
        
        // Execute
        Game createdGame = gameService.createNewGame(privateGame, user.getToken());
        
        // Verify
        assertNotNull(createdGame);
        assertEquals("secret123", createdGame.getPassword());
        assertFalse(createdGame.getIsPublic());
    }
    
    @Test
    void testCreateGamePrivateWithoutPassword() {
        // Setup
        Game invalidPrivateGame = new Game();
        invalidPrivateGame.setCreatorId(user.getId());
        invalidPrivateGame.setIsPublic(false);
        invalidPrivateGame.setMaximalPlayers(5);
        invalidPrivateGame.setStartCredit(1000L);
        invalidPrivateGame.setPassword(null); // Missing password for private game
        invalidPrivateGame.setSmallBlind(5);
        invalidPrivateGame.setBigBlind(10);
        
        // Mock repository behavior
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        
        // Execute and verify
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(invalidPrivateGame, user.getToken());
        });
        
        assertTrue(exception.getMessage().contains("Private games must have a password"));
        verify(gameRepository, never()).save(any(Game.class));
    }
    
    @Test
    void testCreateGamePublicWithPassword() {
        // Setup
        Game publicGameWithPassword = new Game();
        publicGameWithPassword.setCreatorId(user.getId());
        publicGameWithPassword.setIsPublic(true);
        publicGameWithPassword.setMaximalPlayers(5);
        publicGameWithPassword.setStartCredit(1000L);
        publicGameWithPassword.setPassword("unnecessary-password"); // Password should be removed
        publicGameWithPassword.setSmallBlind(5);
        publicGameWithPassword.setBigBlind(10);
        
        // Mock repository to return the saved game
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(publicGameWithPassword);
        
        // Execute
        Game createdGame = gameService.createNewGame(publicGameWithPassword, user.getToken());
        
        // Verify password was removed for public game
        assertNull(createdGame.getPassword());
        assertTrue(createdGame.getIsPublic());
    }
    
    @Test
    void testCreateGameInvalidBlinds() {
        // Setup - Small blind greater than big blind
        Game gameWithInvalidBlinds = new Game();
        gameWithInvalidBlinds.setCreatorId(user.getId());
        gameWithInvalidBlinds.setIsPublic(true);
        gameWithInvalidBlinds.setMaximalPlayers(5);
        gameWithInvalidBlinds.setStartCredit(1000L);
        gameWithInvalidBlinds.setSmallBlind(20); // Invalid - should be less than big blind
        gameWithInvalidBlinds.setBigBlind(10);
        
        // Setup - Negative blinds
        Game gameWithNegativeBlinds = new Game();
        gameWithNegativeBlinds.setCreatorId(user.getId());
        gameWithNegativeBlinds.setIsPublic(true);
        gameWithNegativeBlinds.setMaximalPlayers(5);
        gameWithNegativeBlinds.setStartCredit(1000L);
        gameWithNegativeBlinds.setSmallBlind(-5); // Invalid
        gameWithNegativeBlinds.setBigBlind(10);
        
        // Setup - Zero blinds
        Game gameWithZeroBlinds = new Game();
        gameWithZeroBlinds.setCreatorId(user.getId());
        gameWithZeroBlinds.setIsPublic(true);
        gameWithZeroBlinds.setMaximalPlayers(5);
        gameWithZeroBlinds.setStartCredit(1000L);
        gameWithZeroBlinds.setSmallBlind(0); // Invalid
        gameWithZeroBlinds.setBigBlind(0); // Invalid
        
        // Mock repository behavior
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        
        // Execute and verify - Small blind > big blind
        Exception exception1 = assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithInvalidBlinds, user.getToken());
        });
        assertTrue(exception1.getMessage().contains("Small blind must be less than big blind"));
        
        // Execute and verify - Negative blinds
        Exception exception2 = assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithNegativeBlinds, user.getToken());
        });
        assertTrue(exception2.getMessage().contains("Blind values must be greater than 0"));
        
        // Execute and verify - Zero blinds
        Exception exception3 = assertThrows(ResponseStatusException.class, () -> {
            gameService.createNewGame(gameWithZeroBlinds, user.getToken());
        });
        assertTrue(exception3.getMessage().contains("Blind values must be greater than 0"));
    }
    
    @Test
    void testJoinGameWhenGameAlreadyStarted() {
        // Setup a game that has already started
        Game startedGame = new Game();
        startedGame.setId(5L);
        startedGame.setCreatorId(1L);
        startedGame.setIsPublic(true);
        startedGame.setMaximalPlayers(5);
        startedGame.setStartCredit(1000L);
        startedGame.setGameStatus(GameStatus.PREFLOP); // Game has already started
        
        when(gameRepository.findByid(5L)).thenReturn(startedGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        
        // Execute and verify
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(5L, user.getToken(), null);
        });
        
        assertTrue(exception.getMessage().contains("Game has already started"));
        verify(gameRepository, never()).save(any(Game.class));
    }
    
    @Test
    void testJoinPrivateGameWithWrongPassword() {
        // Setup
        Game privateGame = new Game();
        privateGame.setId(3L);
        privateGame.setCreatorId(1L);
        privateGame.setIsPublic(false);
        privateGame.setMaximalPlayers(5);
        privateGame.setStartCredit(1000L);
        privateGame.setPassword("correct-password");
        privateGame.setGameStatus(GameStatus.READY);
        
        when(gameRepository.findByid(3L)).thenReturn(privateGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        
        // Test with wrong password
        Exception exception1 = assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(3L, user.getToken(), "wrong-password");
        });
        assertTrue(exception1.getMessage().contains("Wrong password"));
        
        // Test with null password
        Exception exception2 = assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(3L, user.getToken(), null);
        });
        assertTrue(exception2.getMessage().contains("Wrong password"));
        
        verify(gameRepository, never()).save(any(Game.class));
    }
    
    @Test
    void testJoinPrivateGameWithCorrectPassword() {
        // Setup
        Game privateGame = new Game();
        privateGame.setId(3L);
        privateGame.setCreatorId(1L);
        privateGame.setIsPublic(false);
        privateGame.setMaximalPlayers(5);
        privateGame.setStartCredit(1000L);
        privateGame.setPassword("correct-password");
        privateGame.setGameStatus(GameStatus.READY);
        privateGame.setPlayers(new ArrayList<>());
        
        when(gameRepository.findByid(3L)).thenReturn(privateGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        when(gameRepository.save(any(Game.class))).thenReturn(privateGame);
        
        // Execute
        gameService.joinGame(3L, user.getToken(), "correct-password");
        
        // Verify
        verify(gameRepository).save(any(Game.class));
        verify(gameRepository).flush();
        
        // Check if user was added as a player
        boolean userFound = false;
        for (Player player : privateGame.getPlayers()) {
            if (player.getUserId().equals(user.getId())) {
                userFound = true;
                break;
            }
        }
        assertTrue(userFound, "User should be added as a player");
    }
    /*
    @Test
    void testJoinFullGame() {
        // Setup a game that is already full
        Game fullGame = new Game();
        fullGame.setId(6L);
        fullGame.setCreatorId(1L);
        fullGame.setIsPublic(true);
        fullGame.setMaximalPlayers(3);
        fullGame.setStartCredit(1000L);
        fullGame.setGameStatus(GameStatus.READY);
        
        List<Player> players = new ArrayList<>();
        players.add(new Player(1L, new ArrayList<>(), fullGame));
        players.add(new Player(2L, new ArrayList<>(), fullGame));
        players.add(new Player(3L, new ArrayList<>(), fullGame)); // Game is full with 3 players
        fullGame.setPlayers(players);
        
        when(gameRepository.findByid(6L)).thenReturn(fullGame);
        when(userRepository.findByToken(user.getToken())).thenReturn(user);
        
        // Execute and verify
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.joinGame(6L, user.getToken(), null);
        });
        
        assertTrue(exception.getMessage().contains("Game is full"));
        verify(gameRepository, never()).save(any(Game.class));
    }
         */
}