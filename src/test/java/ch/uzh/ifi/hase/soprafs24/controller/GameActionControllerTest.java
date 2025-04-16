package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ProbabilityResponse;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
} 