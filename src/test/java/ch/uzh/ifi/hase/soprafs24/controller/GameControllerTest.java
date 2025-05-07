package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserFriendsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;

@WebMvcTest(GameRoomController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean 
    private GameService gameService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserFriendsService userFriendsService;

    @Test
    public void getAllPublicGamesValidTokenTest() throws Exception {
        // given user
        User user= new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(java.time.LocalDate.now());
        user.setToken("valid-token");

        // given game
        Game game = new Game();
        game.setId(1L);
        game.setIsPublic(true);
        game.setSmallBlind(1);
        game.setBigBlind(1);
        game.setSmallBlindIndex(0);
        game.setStartCredit(1L);
        game.setMaximalPlayers(3);
        game.setPot(1L);
        game.setCallAmount(1L);
        game.setPlayers(Collections.emptyList());
        game.setStatus(GameStatus.WAITING);
        game.setCommunityCards(Collections.emptyList());
        game.setCreatorId(1L);        

        List<Game> allGames = Collections.singletonList(game);

        given(gameService.getAllPublicGames(user.getToken())).willReturn(allGames);
        
        

        // when
        MockHttpServletRequestBuilder getRequest = get("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.getToken());

        // then
        mockMvc.perform(getRequest)

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(game.getId()))
                .andExpect(jsonPath("$[0].creatorId").value(game.getCreatorId()))
                .andExpect(jsonPath("$[0].isPublic").value(game.getIsPublic()))
                .andExpect(jsonPath("$[0].smallBlind").value(game.getSmallBlind()))
                .andExpect(jsonPath("$[0].bigBlind").value(game.getBigBlind()))
                .andExpect(jsonPath("$[0].smallBlindIndex").value(game.getSmallBlindIndex()))
                .andExpect(jsonPath("$[0].startCredit").value(game.getStartCredit()))
                .andExpect(jsonPath("$[0].maximalPlayers").value(game.getMaximalPlayers()))
                .andExpect(jsonPath("$[0].pot").value(game.getPot()))
                .andExpect(jsonPath("$[0].callAmount").value(game.getCallAmount()))
                .andExpect(jsonPath("$[0].players").isEmpty())
                .andExpect(jsonPath("$[0].gameStatus").value(game.getStatus().toString()))
                .andExpect(jsonPath("$[0].communityCards").isEmpty());
                
    }


    @Test
    public void getGameByIdValidTokenTest() throws Exception {

            // given user
            User user= new User();
            user.setName("Firstname Lastname");
            user.setUsername("firstname@lastname");
            user.setStatus(UserStatus.OFFLINE);
            user.setCreationDate(java.time.LocalDate.now());
            user.setToken("valid-token");
    
            // given game
            Game game = new Game();
            game.setId(1L);
            game.setIsPublic(true);
            game.setSmallBlind(1);
            game.setBigBlind(1);
            game.setSmallBlindIndex(0);
            game.setStartCredit(1L);
            game.setMaximalPlayers(3);
            game.setPot(1L);
            game.setCallAmount(1L);
            game.setPlayers(Collections.emptyList());
            game.setStatus(GameStatus.WAITING);
            game.setCommunityCards(Collections.emptyList());
            game.setCreatorId(1L);        

            // when
            given(gameService.getGameById(game.getId(), user.getToken())).willReturn(game);
            MockHttpServletRequestBuilder getRequest = get("/games/" + game.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + user.getToken());
            // then
            mockMvc.perform(getRequest)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(game.getId()))
                    .andExpect(jsonPath("$.creatorId").value(game.getCreatorId()))
                    .andExpect(jsonPath("$.isPublic").value(game.getIsPublic()))
                    .andExpect(jsonPath("$.smallBlind").value(game.getSmallBlind()))
                    .andExpect(jsonPath("$.bigBlind").value(game.getBigBlind()))
                    .andExpect(jsonPath("$.smallBlindIndex").value(game.getSmallBlindIndex()))
                    .andExpect(jsonPath("$.startCredit").value(game.getStartCredit()))
                    .andExpect(jsonPath("$.maximalPlayers").value(game.getMaximalPlayers()))
                    .andExpect(jsonPath("$.pot").value(game.getPot()))
                    .andExpect(jsonPath("$.callAmount").value(game.getCallAmount()))
                    .andExpect(jsonPath("$.players").isEmpty())
                    .andExpect(jsonPath("$.gameStatus").value(game.getStatus().toString()))
                    .andExpect(jsonPath("$.communityCards").isEmpty());

    }
        
    @Test
    public void testJoinGameTwiceNoDuplicatePlayers() throws Exception {
        // given user
        User user = new User();
        user.setId(1L);
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(java.time.LocalDate.now());
        user.setToken("valid-token");

        // given game
        Game game = new Game();
        game.setId(1L);
        game.setIsPublic(true);
        game.setSmallBlind(1);
        game.setBigBlind(1);
        game.setSmallBlindIndex(0);
        game.setStartCredit(1L);
        game.setMaximalPlayers(3);
        game.setPot(1L);
        game.setCallAmount(1L);
        game.setPlayers(new ArrayList<>());
        game.setStatus(GameStatus.WAITING);
        game.setCommunityCards(new ArrayList<>());
        game.setCreatorId(1L);

        // Create a game with one player for the second getGameById call
        Game gameWithPlayer = new Game();
        gameWithPlayer.setId(1L);
        gameWithPlayer.setIsPublic(true);
        gameWithPlayer.setSmallBlind(1);
        gameWithPlayer.setBigBlind(1);
        gameWithPlayer.setSmallBlindIndex(0);
        gameWithPlayer.setStartCredit(1L);
        gameWithPlayer.setMaximalPlayers(3);
        gameWithPlayer.setPot(1L);
        gameWithPlayer.setCallAmount(1L);
        gameWithPlayer.setStatus(GameStatus.WAITING);
        gameWithPlayer.setCommunityCards(new ArrayList<>());
        gameWithPlayer.setCreatorId(1L);
        
        // Add a player to the game
        List<Player> players = new ArrayList<>();
        Player player = new Player(user.getId(), new ArrayList<>(), gameWithPlayer);
        players.add(player);
        gameWithPlayer.setPlayers(players);

        // when
        // First getGameById returns empty game, second returns game with player
        given(gameService.getGameById(game.getId(), user.getToken()))
            .willReturn(game)
            .willReturn(gameWithPlayer);
        Mockito.doNothing().when(gameService).joinGame(game.getId(), user.getToken(), null);

        // First join
        MockHttpServletRequestBuilder firstJoinRequest = post("/games/" + game.getId() + "/join")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.getToken())
                .content("{\"password\": null}");

        // Second join
        MockHttpServletRequestBuilder secondJoinRequest = post("/games/" + game.getId() + "/join")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.getToken())
                .content("{\"password\": null}");

        // then
        // First join should succeed
        mockMvc.perform(firstJoinRequest)
                .andExpect(status().isOk());

        // Second join should also succeed but not add another player
        mockMvc.perform(secondJoinRequest)
                .andExpect(status().isOk());

        // Verify that getGameById was called twice
        verify(gameService, times(2)).getGameById(game.getId(), user.getToken());
        // Verify that joinGame was called twice
        verify(gameService, times(2)).joinGame(game.getId(), user.getToken(), null);
        // Verify that the game has exactly one player
        assertEquals(1, gameWithPlayer.getPlayers().size());
    }
    


    @Test
    public void deleteGameTest() throws Exception{

        // given game
        Game game = new Game();
        game.setId(1L);
        game.setIsPublic(true);
        game.setSmallBlind(1);
        game.setBigBlind(1);
        game.setSmallBlindIndex(0);
        game.setStartCredit(1L);
        game.setMaximalPlayers(3);
        game.setPot(1L);
        game.setCallAmount(1L);
        game.setPlayers(Collections.emptyList());
        game.setStatus(GameStatus.WAITING);
        game.setCommunityCards(Collections.emptyList());
        game.setCreatorId(1L);    

        // given user
        User user= new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(java.time.LocalDate.now());
        user.setToken("valid-token");


        // when
        given(gameService.deleteGame(game.getId(), user.getToken())).willReturn(game);
        MockHttpServletRequestBuilder deleteRequest = delete("/games/" + game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.getToken());

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.creatorId").value(game.getCreatorId()))
                .andExpect(jsonPath("$.isPublic").value(game.getIsPublic()))
                .andExpect(jsonPath("$.smallBlind").value(game.getSmallBlind()))
                .andExpect(jsonPath("$.bigBlind").value(game.getBigBlind()))
                .andExpect(jsonPath("$.smallBlindIndex").value(game.getSmallBlindIndex()))
                .andExpect(jsonPath("$.startCredit").value(game.getStartCredit()))
                .andExpect(jsonPath("$.maximalPlayers").value(game.getMaximalPlayers()))
                .andExpect(jsonPath("$.pot").value(game.getPot()))
                .andExpect(jsonPath("$.callAmount").value(game.getCallAmount()))
                .andExpect(jsonPath("$.players").isEmpty())
                .andExpect(jsonPath("$.gameStatus").value(game.getStatus().toString()))
                .andExpect(jsonPath("$.communityCards").isEmpty());
    }
}
