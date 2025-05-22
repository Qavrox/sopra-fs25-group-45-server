package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerActionPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PokerAdviceResponseDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameActionController.class)
public class GameActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserService userService;

    @Test
    public void getWinProbability_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(1L, new ArrayList<>(), game);
        players.add(player);
        game.setPlayers(players);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.calculateWinProbability(eq(1L), eq(1L))).willReturn(0.75);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/probability")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probability").value(0.75));
    }

    @Test
    public void getWinProbability_invalidToken() throws Exception {
        // given
        given(userService.getUserByToken("invalid-token")).willReturn(null);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/probability")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getWinProbability_gameNotFound() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/probability")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getWinProbability_notAPlayer() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(2L, new ArrayList<>(), game); // Different user ID
        players.add(player);
        game.setPlayers(players);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/probability")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void startBettingRound_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setCreatorId(1L); // User is the creator of the game

        Game updatedGame = new Game();
        updatedGame.setId(1L);
        updatedGame.setGameStatus(GameStatus.PREFLOP);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.startBettingRound(eq(1L))).willReturn(updatedGame);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/start-betting")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gameStatus").value(GameStatus.PREFLOP.toString()));
    }

    @Test
    public void startBettingRound_userIsNotCreator() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setCreatorId(2L); // Different user is the creator

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/start-betting")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void performPlayerAction_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(1L, new ArrayList<>(), game);
        players.add(player);
        game.setPlayers(players);

        Game updatedGame = new Game();
        updatedGame.setId(1L);
        updatedGame.setPlayers(players);
        // Add more state to represent the result of the action

        PlayerActionPostDTO actionDTO = new PlayerActionPostDTO();
        actionDTO.setUserId(1L);
        actionDTO.setAction(PlayerAction.CALL);
        actionDTO.setAmount(10L);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.processPlayerAction(eq(1L), eq(1L), eq(PlayerAction.CALL), eq(10L))).willReturn(updatedGame);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/action")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(actionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void performPlayerAction_notAPlayer() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(2L, new ArrayList<>(), game); // Different user ID
        players.add(player);
        game.setPlayers(players);

        PlayerActionPostDTO actionDTO = new PlayerActionPostDTO();
        actionDTO.setUserId(3L); // This ID doesn't match any player in the game
        actionDTO.setAction(PlayerAction.CALL);
        actionDTO.setAmount(10L);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/action")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(actionDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getGameResults_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.GAMEOVER);
        
        List<Player> players = new ArrayList<>();
        Player winningPlayer = new Player(1L, new ArrayList<>(), game);
        players.add(winningPlayer);
        game.setPlayers(players);
        game.setWinners(Arrays.asList(winningPlayer));

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.getHandDescription(eq(winningPlayer), any())).willReturn("Royal Flush");

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winningHand").value("Royal Flush"))
                .andExpect(jsonPath("$.statistics").exists())
                .andExpect(jsonPath("$.statistics.potsWon").value(1));
    }

    @Test
    public void getGameResults_gameNotOver() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.PREFLOP);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getGameResults_noWinners() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.GAMEOVER);
        game.setWinners(new ArrayList<>()); // No winners

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getPokerAdvice_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(1L, new ArrayList<>(), game);
        players.add(player);
        game.setPlayers(players);

        String adviceText = "With your pair of Aces, consider raising to build the pot.";
        
        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.getPokerAdvice(eq(1L), eq(1L))).willReturn(adviceText);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/advice")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advice").value(adviceText));
    }

    @Test
    public void getPokerAdvice_notAPlayer() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        List<Player> players = new ArrayList<>();
        Player player = new Player(2L, new ArrayList<>(), game); // Different user ID
        players.add(player);
        game.setPlayers(players);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/advice")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void multiplePlayerScenario_probabilitySuccess() throws Exception {
        // Setup a game with multiple players
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        
        List<Player> players = new ArrayList<>();
        Player player1 = new Player(1L, new ArrayList<>(), game); // User's player
        Player player2 = new Player(2L, new ArrayList<>(), game);
        Player player3 = new Player(3L, new ArrayList<>(), game);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        game.setPlayers(players);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.calculateWinProbability(eq(1L), eq(1L))).willReturn(0.33); // 1/3 chance with 3 players

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/probability")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probability").value(0.33));
    }

    @Test
    public void multiplePlayerScenario_actionSuccess() throws Exception {
        // Setup a game with multiple players
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        
        List<Player> players = new ArrayList<>();
        Player player1 = new Player(1L, new ArrayList<>(), game); // User's player
        Player player2 = new Player(2L, new ArrayList<>(), game);
        Player player3 = new Player(3L, new ArrayList<>(), game);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        game.setPlayers(players);

        Game updatedGame = new Game();
        updatedGame.setId(1L);
        updatedGame.setPlayers(players);
        // Add state representing the action result

        PlayerActionPostDTO actionDTO = new PlayerActionPostDTO();
        actionDTO.setUserId(1L);
        actionDTO.setAction(PlayerAction.RAISE);
        actionDTO.setAmount(20L);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.processPlayerAction(eq(1L), eq(1L), eq(PlayerAction.RAISE), eq(20L))).willReturn(updatedGame);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/action")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(actionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void multiplePlayerScenario_resultsWithParticipationRate() throws Exception {
        // Setup a game with multiple players, some folded
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.GAMEOVER);
        
        List<Player> players = new ArrayList<>();
        Player player1 = new Player(1L, new ArrayList<>(), game); // Winner
        Player player2 = new Player(2L, new ArrayList<>(), game);
        player2.setHasFolded(true); // Folded
        Player player3 = new Player(3L, new ArrayList<>(), game);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        game.setPlayers(players);
        game.setWinners(Arrays.asList(player1));

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.getHandDescription(eq(player1), any())).willReturn("Two Pair");

        // when/then - participation rate should be 2/3 = ~0.67 (2 active players out of 3)
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winningHand").value("Two Pair"))
                .andExpect(jsonPath("$.statistics.participationRate").value(2.0 / 3.0));
    }

    // Helper method to convert objects to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
} 