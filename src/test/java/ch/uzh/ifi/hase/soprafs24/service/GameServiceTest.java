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
import java.util.Optional;

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
    private Player player1;
    private Player player2;


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
        player1 = new Player(1L, new ArrayList<>(), game);
        player2 = new Player(2L, new ArrayList<>(), game);
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
        when(playerRepository.findByid(1L)).thenReturn(player1);
        when(playerRepository.findByid(2L)).thenReturn(player2);

      
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
    public void testGameDeletionValidToken(){
        // Delete the game
        gameService.deleteGame(game.getId(), user.getToken());
        
        assertEquals(game.getStatus(), GameStatus.ARCHIEVED);
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getGameById(game.getId(), user.getToken());
        });
    }

    @Test
    public void testGameDeletionInvalidToken(){

        assertThrows(ResponseStatusException.class, () -> {
            gameService.deleteGame(game.getId(), "invalid token");
        });
    }    

    @Test
    void testLeaveGameValidPlayerToken() {

            // Given new game such that creatorId does not match with user
            Game leftGame = new Game();
            leftGame.setId(1L);
            leftGame.setGameStatus(GameStatus.RIVER);
            leftGame.setMaximalPlayers(5);
            leftGame.setStartCredit(1000L);
            leftGame.setCreatorId(2L);
            leftGame.setIsPublic(true);
            leftGame.setSmallBlind(1);
            leftGame.setBigBlind(1);
            leftGame.setSmallBlindIndex(0);
            leftGame.setPot(1L);
            leftGame.setCallAmount(1L);
        
        

            // Call leaveGame
            Game updatedGame = gameService.leaveGame(leftGame.getId(), user.getToken());
            when(gameRepository.findByid(leftGame.getId())).thenReturn(updatedGame);
            

            // Verify the player was removed from the game
            assertNotNull(updatedGame);
            assertFalse(leftGame.getPlayers().contains(player1));
            assertEquals(GameStatus.ARCHIEVED, updatedGame.getStatus()); // Game status should remain unchanged
        }


    @Test
    void testLeaveGameGameNotFound() {
        assertThrows(ResponseStatusException.class, () -> {
            gameService.leaveGame(2L, user.getToken()); // Game with ID 2 does not exist
        });
    }


    @Test
    void testLeaveGameCreatorLeavesGame() {

        // Call leaveGame
        Game updatedGame = gameService.leaveGame(game.getId(), user.getToken());

        // Assert: Verify the game was archived
        assertNotNull(updatedGame);
        assertEquals(GameStatus.ARCHIEVED, updatedGame.getStatus());
    }




}