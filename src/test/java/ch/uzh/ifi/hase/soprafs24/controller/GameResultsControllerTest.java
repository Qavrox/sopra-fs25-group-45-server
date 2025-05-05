package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameActionController.class)
public class GameResultsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserService userService;

    @Test
    public void getGameResults_success() throws Exception {
        // Set up user
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        // Set up game
        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.GAMEOVER);
        
        // Set up players
        Player winner = new Player(1L, Arrays.asList("AH", "AD"), game);
        winner.setCredit(1000L);
        winner.setHasFolded(false);
        
        Player loser = new Player(2L, Arrays.asList("2H", "3D"), game);
        loser.setCredit(500L);
        loser.setHasFolded(false);
        
        List<Player> players = Arrays.asList(winner, loser);
        game.setPlayers(players);
        
        // Set up community cards
        List<String> communityCards = Arrays.asList("AC", "AS", "5H", "8D", "JC");
        game.setCommunityCards(communityCards);
        
        // Set up mocks
        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.determineWinners(eq(1L))).willReturn(Arrays.asList(winner));
        given(gameService.getHandDescription(eq(winner), eq(communityCards))).willReturn("Four of a Kind: Aces");

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winner.userId").value(1))
                .andExpect(jsonPath("$.winningHand").value("Four of a Kind: Aces"))
                .andExpect(jsonPath("$.statistics.participationRate").value(1.0))
                .andExpect(jsonPath("$.statistics.potsWon").value(1));
    }

    @Test
    public void getGameResults_invalidToken() throws Exception {
        // given
        given(userService.getUserByToken("invalid-token")).willReturn(null);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getGameResults_gameNotFound() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getGameResults_gameNotFinished() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.PREFLOP); // Game is not over

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getGameResults_noWinnersFound() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setToken("valid-token");

        Game game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.GAMEOVER);

        given(userService.getUserByToken("valid-token")).willReturn(user);
        given(gameService.getGameById(eq(1L), any())).willReturn(game);
        given(gameService.determineWinners(eq(1L))).willReturn(new ArrayList<>());

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/results")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isInternalServerError());
    }
} 