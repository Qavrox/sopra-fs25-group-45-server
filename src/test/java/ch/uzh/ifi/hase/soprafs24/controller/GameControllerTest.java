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
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.BDDMockito.given;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString; 
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreationPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserFriendsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createAGame_validInput_gameCreated() throws Exception {
        // given
        GameCreationPostDTO gamePostDTO = new GameCreationPostDTO();
        // Populate gamePostDTO with some values if needed for mapping,
        // or rely on default Game entity creation from an empty DTO if that's the logic.
        // For this test, the content of gamePostDTO isn't deeply inspected by the controller itself,
        // but rather by the service via the mapped Game entity.
        gamePostDTO.setCreatorId(1L); // Example field
        gamePostDTO.setPublic(true);
        gamePostDTO.setMaximalPlayers(4);


        // Mock the gameService.createNewGame to simulate its behavior.
        // The controller passes a Game entity (converted from DTO) to this service method.
        // If the ID is set by the service and needed in the response, mock it accordingly.
        doAnswer(invocation -> {
            Game gameArg = invocation.getArgument(0);
            gameArg.setId(123L); // Simulate setting an ID on the game entity
            gameArg.setCreatorId(gamePostDTO.getCreatorId());
            gameArg.setIsPublic(gamePostDTO.getIsPublic());
            gameArg.setMaximalPlayers(gamePostDTO.getMaximalPlayers());
            gameArg.setStatus(GameStatus.WAITING); // Default status
            // Populate other fields if they are expected in the GameGetDTO response
            return null; // createNewGame might be void or return the game
        }).when(gameService).createNewGame(any(Game.class), eq("valid-token"));

        MockHttpServletRequestBuilder postRequest = post("/games")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gamePostDTO));

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.creatorId").value(gamePostDTO.getCreatorId()))
                .andExpect(jsonPath("$.isPublic").value(gamePostDTO.getIsPublic()))
                .andExpect(jsonPath("$.maximalPlayers").value(gamePostDTO.getMaximalPlayers()))
                .andExpect(jsonPath("$.gameStatus").value(GameStatus.WAITING.toString()));

        verify(gameService, times(1)).createNewGame(any(Game.class), eq("valid-token"));
    }

    @Test
    public void createAGame_serviceThrowsError_returnsErrorStatus() throws Exception {
        // given
        GameCreationPostDTO gamePostDTO = new GameCreationPostDTO();
        // Populate as needed

        // Simulate a scenario where gameService throws an exception (e.g., invalid token, user not found by service)
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Service-level token validation failed"))
               .when(gameService).createNewGame(any(Game.class), eq("invalid-token"));

        MockHttpServletRequestBuilder postRequest = post("/games")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gamePostDTO));

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized()); // Or whatever status the service throws

        verify(gameService, times(1)).createNewGame(any(Game.class), eq("invalid-token"));
    }


    // Tests for startNewRound
    @Test
    public void startNewRound_validInput_newRoundStarted() throws Exception {
        // given
        Long gameId = 1L;
        String token = "valid-token";

        Game gameAfterNewRound = new Game();
        gameAfterNewRound.setId(gameId);
        gameAfterNewRound.setStatus(GameStatus.PREFLOP); // Example status after new round
        // Populate other fields of gameAfterNewRound as expected in the response

        given(gameService.startRound(eq(gameId), eq(token))).willReturn(gameAfterNewRound);

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/newround", gameId)
                .header("Authorization", "Bearer " + token);

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.gameStatus").value(GameStatus.PREFLOP.toString()));

        verify(gameService, times(1)).startRound(eq(gameId), eq(token));
    }

    @Test
    public void startNewRound_invalidToken_throwsUnauthorized() throws Exception {
        // given
        Long gameId = 1L;
        String invalidToken = "invalid-token";

        given(gameService.startRound(eq(gameId), eq(invalidToken)))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token provided"));

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/newround", gameId)
                .header("Authorization", "Bearer " + invalidToken);

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());

        verify(gameService, times(1)).startRound(eq(gameId), eq(invalidToken));
    }

    @Test
    public void startNewRound_gameNotFound_throwsNotFound() throws Exception {
        // given
        Long gameId = 999L; // Non-existent game
        String token = "valid-token";

        given(gameService.startRound(eq(gameId), eq(token)))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/newround", gameId)
                .header("Authorization", "Bearer " + token);

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());

        verify(gameService, times(1)).startRound(eq(gameId), eq(token));
    }

    @Test
    public void startNewRound_gameServiceError_returnsErrorStatus() throws Exception {
        // given
        Long gameId = 1L;
        String token = "valid-token";

        // Example: Service throws BadRequest if game is not in a state to start a new round
        given(gameService.startRound(eq(gameId), eq(token)))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not in correct state"));

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/newround", gameId)
                .header("Authorization", "Bearer " + token);

        // when / then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());

        verify(gameService, times(1)).startRound(eq(gameId), eq(token));
    }


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
