package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.helpers.SecretManagerHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameServiceLeaveGameTest {

    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameHistoryService gameHistoryService;

    @Mock
    private SecretManagerHelper secretManagerHelper;

    @Mock
    private Authenticator authenticator;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        gameService = new GameService(
            gameRepository,
            userRepository,
            playerRepository,
            gameHistoryService,
            secretManagerHelper
        );

        Field authField = GameService.class.getDeclaredField("authenticator");
        authField.setAccessible(true);
        authField.set(gameService, authenticator);
    }

    @Test
    public void leaveGame_removesPlayerAndSavesGame() {
        // Arrange
        String token = "valid-token";
        Long gameId = 42L;
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);

        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getUserId()).thenReturn(userId);

        Game mockGame = new Game();
        mockGame.setStatus(GameStatus.GAMEOVER);
        List<Player> playerList = new ArrayList<>();
        playerList.add(mockPlayer);
        mockGame.setPlayers(playerList);

        when(userRepository.findByToken(token)).thenReturn(mockUser);
        when(gameRepository.findByid(gameId)).thenReturn(mockGame);
        when(gameRepository.save(mockGame)).thenReturn(mockGame);
        doNothing().when(authenticator).checkTokenValidity(token);
        doNothing().when(playerRepository).delete(mockPlayer);
        doNothing().when(playerRepository).flush();
        doNothing().when(gameRepository).flush();

        // Act
        gameService.leaveGame(gameId, token);

        // Assert
        assertFalse(mockGame.getPlayers().contains(mockPlayer));
        verify(authenticator).checkTokenValidity(token);
        verify(gameRepository).save(mockGame);
    }
    @Test
    public void leaveGame_gameNotOver_throwsForbidden() {
        // Arrange
        String token = "valid-token";
        Long gameId = 42L;
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);

        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getUserId()).thenReturn(userId);

        Game mockGame = new Game();
        mockGame.setStatus(GameStatus.PREFLOP); // Not GAMEOVER
        List<Player> playerList = new ArrayList<>();
        playerList.add(mockPlayer);
        mockGame.setPlayers(playerList);

        when(userRepository.findByToken(token)).thenReturn(mockUser);
        when(gameRepository.findByid(gameId)).thenReturn(mockGame);
        doNothing().when(authenticator).checkTokenValidity(token);

        // Act + Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            gameService.leaveGame(gameId, token)
        );

        assertTrue(exception.getMessage().contains("not allowed to leave"));
        verify(authenticator).checkTokenValidity(token);
    }

    @Test
    public void leaveGame_userNotFound_throwsNotFound() {
        String token = "invalid-token";
        Long gameId = 1L;

        doNothing().when(authenticator).checkTokenValidity(token);
        when(userRepository.findByToken(token)).thenReturn(null); // simulate not found

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            gameService.leaveGame(gameId, token)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("User not found"));
    }

    @Test
    public void leaveGame_gameIsArchived_throwsNotFound() {
        String token = "valid-token";
        Long gameId = 1L;
        Long userId = 99L;

        User mockUser = new User();
        mockUser.setId(userId);

        Game archivedGame = new Game();
        archivedGame.setStatus(GameStatus.ARCHIVED);

        doNothing().when(authenticator).checkTokenValidity(token);
        when(userRepository.findByToken(token)).thenReturn(mockUser);
        when(gameRepository.findByid(gameId)).thenReturn(archivedGame); // simulate archived game

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            gameService.leaveGame(gameId, token)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("Game not found"));
    }

    @Test
    public void leaveGame_playerNotInGame_throwsNotFound() {
        String token = "valid-token";
        Long gameId = 1L;
        Long userId = 123L;

        User mockUser = new User();
        mockUser.setId(userId);

        Player unrelatedPlayer = mock(Player.class);
        when(unrelatedPlayer.getUserId()).thenReturn(999L); // different user

        Game mockGame = new Game();
        mockGame.setStatus(GameStatus.GAMEOVER);
        List<Player> players = new ArrayList<>();
        players.add(unrelatedPlayer);
        mockGame.setPlayers(players);

        doNothing().when(authenticator).checkTokenValidity(token);
        when(userRepository.findByToken(token)).thenReturn(mockUser);
        when(gameRepository.findByid(gameId)).thenReturn(mockGame);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            gameService.leaveGame(gameId, token)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("Player not part of this game"));
    }

    @Test
    public void leaveGame_gameNotFound_throwsNotFound() {
        String token = "valid-token";
        Long gameId = 123L;
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);

        doNothing().when(authenticator).checkTokenValidity(token);
        when(userRepository.findByToken(token)).thenReturn(mockUser);
        when(gameRepository.findByid(gameId)).thenReturn(null); // simulate game not found

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            gameService.leaveGame(gameId, token)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains("Game not found"));
    }


}