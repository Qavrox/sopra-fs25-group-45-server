package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRoomController.class)
class GameRoomControllerLeaveGameTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    /** happy-path: service succeeds → 200 OK */
    @Test
    void leaveGame_validRequest_returnsOk() throws Exception {
        Long gameId = 1L;
        String token = "Bearer goodToken";

        mockMvc.perform(delete("/games/{gameId}/join", gameId)
                        .header("Authorization", token))
               .andExpect(status().isOk());

        // token is stripped inside the controller → “goodToken”
        verify(gameService, times(1)).leaveGame(gameId, "goodToken");
    }

    /** Service throws 404 → controller maps to 404 */
    @Test
    void leaveGame_gameNotFound_returns404() throws Exception {
        Long gameId = 99L;
        String token = "Bearer badToken";

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(gameService).leaveGame(gameId, "badToken");

        mockMvc.perform(delete("/games/{gameId}/join", gameId)
                        .header("Authorization", token))
               .andExpect(status().isNotFound());
    }
}
