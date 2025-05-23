package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameHistoryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserStatisticsDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
// import com.fasterxml.jackson.databind.ObjectMapper; // Not strictly needed for these GET requests
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameHistoryController.class)
public class GameHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameHistoryService gameHistoryService;

    @MockBean
    private UserService userService;

    private User mockUser;
    private final Long mockUserId = 1L; 

    // Define token strings clearly
    private final String VALID_RAW_TOKEN = "valid-test-token";
    private final String VALID_HEADER_TOKEN = "Bearer " + VALID_RAW_TOKEN;

    private final String INVALID_RAW_TOKEN = "invalid-token";
    private final String INVALID_HEADER_TOKEN = "Bearer " + INVALID_RAW_TOKEN;

    private final String UNAUTH_RAW_TOKEN = "unauthenticated-user-token";
    private final String UNAUTH_HEADER_TOKEN = "Bearer " + UNAUTH_RAW_TOKEN;


    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(mockUserId); 
        mockUser.setToken(VALID_RAW_TOKEN); 
        mockUser.setUsername("testUser");
    }

    // Test for getUserGameHistory
    @Test
    public void getUserGameHistory_validRequest_returnsHistory() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);

        GameHistory gameHistoryEntry = new GameHistory();
        gameHistoryEntry.setGameId(100L);
        gameHistoryEntry.setUserId(mockUserId); 
        gameHistoryEntry.setWinnings(500L);
        gameHistoryEntry.setPlayedAt(LocalDateTime.now().minusDays(1));

        List<GameHistory> histories = Collections.singletonList(gameHistoryEntry);
        given(gameHistoryService.getUserGameHistory(mockUserId)).willReturn(histories); 

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history", mockUserId)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameId").value(gameHistoryEntry.getGameId().intValue()));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserGameHistory(mockUserId); 
    }

    @Test
    public void getUserGameHistory_userMismatch_throwsForbidden() throws Exception {
        Long otherUserId = 2L;
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser); 

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history", otherUserId)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isForbidden()); 
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserGameHistory_invalidToken_throwsUnauthorized() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"))
                .when(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history", mockUserId)
                .header("Authorization", INVALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));
    }

    @Test
    public void getUserGameHistory_emptyHistory_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        given(gameHistoryService.getUserGameHistory(mockUserId)).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history", mockUserId)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserGameHistory(mockUserId);
    }

    // Test for getUserGameHistoryByTimeRange
    @Test
    public void getUserGameHistoryByTimeRange_validRequest_returnsHistory() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);

        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        List<GameHistory> histories = Collections.singletonList(new GameHistory());
        given(gameHistoryService.getUserGameHistoryByTimeRange(mockUserId, startDate, endDate)).willReturn(histories); 

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", mockUserId) 
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserGameHistoryByTimeRange(mockUserId, startDate, endDate); 
    }

    @Test
    public void getUserGameHistoryByTimeRange_userMismatch_throwsForbidden() throws Exception {
        Long otherUserId = 2L;
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);

        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", otherUserId)
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isForbidden());
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserGameHistoryByTimeRange_invalidToken_throwsUnauthorized() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"))
                .when(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", mockUserId)
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", INVALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));
    }

    @Test
    public void getUserGameHistoryByTimeRange_emptyHistory_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        given(gameHistoryService.getUserGameHistoryByTimeRange(mockUserId, startDate, endDate))
                .willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", mockUserId)
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserGameHistoryByTimeRange(mockUserId, startDate, endDate);
    }

    @Test
    public void getUserGameHistoryByTimeRange_invalidStartDate_throwsBadRequest() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        String invalidStartDateStr = "not-a-date";
        String endDateStr = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", mockUserId)
                .param("startDateStr", invalidStartDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
        // No need to verify userService.getUserByToken if Spring handles date parsing error before controller method body
        // However, the current controller code does token check first, then date parsing.
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserGameHistoryByTimeRange_invalidEndDate_throwsBadRequest() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        String startDateStr = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_DATE_TIME);
        String invalidEndDateStr = "not-a-date-either";

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/history/range", mockUserId)
                .param("startDateStr", startDateStr)
                .param("endDateStr", invalidEndDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    // Test for getUserStatistics
    @Test
    public void getUserStatistics_allTime_returnsStatistics() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setGamesPlayed(10L);
        stats.setTotalWinnings(1000L);
        given(gameHistoryService.getUserStatistics(mockUserId, null, null)).willReturn(stats); 

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId) 
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamesPlayed").value(10L));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserStatistics(mockUserId, null, null); 
    }
    


    @Test
    public void getUserStatistics_withDateRange_returnsStatistics() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateStr = endDate.format(DateTimeFormatter.ISO_DATE_TIME);

        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setGamesPlayed(5L);
        given(gameHistoryService.getUserStatistics(mockUserId, startDate, endDate)).willReturn(stats); 

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId) 
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamesPlayed").value(5L));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserStatistics(mockUserId, startDate, endDate); 
    }

    @Test
    public void getUserStatistics_onlyStartDate_throwsBadRequest() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser); 
        String startDateStr = LocalDateTime.now().minusDays(10).format(DateTimeFormatter.ISO_DATE_TIME);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId) 
                .param("startDateStr", startDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest()); 
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserStatistics_invalidToken_throwsUnauthorized() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"))
                .when(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId)
                .header("Authorization", INVALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));
    }

    @Test
    public void getUserStatistics_onlyEndDate_throwsBadRequest() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        String endDateStr = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId)
                .param("endDateStr", endDateStr)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserStatistics_invalidDateRangeFormat_throwsBadRequest() throws Exception {
        // This method in controller has try-catch for date parsing, so it should return 400
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser); 
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId) 
                .param("startDateStr", "invalid-date")
                .param("endDateStr", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest()); 
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    @Test
    public void getUserStatistics_noStatsFound_returnsEmptyStats() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        UserStatisticsDTO emptyStats = new UserStatisticsDTO(); // Assuming default constructor zeroes fields or they are nullable
        // Explicitly set fields to expected "empty" values if necessary, e.g.:
        // emptyStats.setGamesPlayed(0L);
        // emptyStats.setTotalWinnings(0L);
        // etc.
        given(gameHistoryService.getUserStatistics(mockUserId, null, null)).willReturn(emptyStats);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", mockUserId)
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gamesPlayed").isEmpty()) // Expect null or not present
                .andExpect(jsonPath("$.totalWinnings").isEmpty()); // Expect null or not present
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getUserStatistics(mockUserId, null, null);
    }

    @Test
    public void getUserStatistics_userMismatch_throwsForbidden() throws Exception {
        Long otherUserId = 2L;
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser); // mockUser.getId() is mockUserId (1L)

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/statistics", otherUserId) // Requesting for otherUserId
                .header("Authorization", VALID_HEADER_TOKEN) // Token is for mockUserId
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isForbidden());
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
    }

    // Test for getLeaderboardByWinnings
    @Test
    public void getLeaderboardByWinnings_validToken_returnsLeaderboard() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser); 
        List<LeaderboardEntryDTO> leaderboard = Collections.singletonList(new LeaderboardEntryDTO());
        given(gameHistoryService.getLeaderboardByWinnings()).willReturn(leaderboard);

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winnings")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getLeaderboardByWinnings();
    }

    @Test
    public void getLeaderboardByWinnings_emptyLeaderboard_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        given(gameHistoryService.getLeaderboardByWinnings()).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winnings")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getLeaderboardByWinnings();
    }

    @Test
    public void getLeaderboardByWinnings_invalidToken_throwsUnauthorized() throws Exception {
        // Controller calls userService.getUserByToken(fullToken) but doesn't check for null.
        // Service itself should throw if token is truly invalid and unfindable.
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token from service"))
                .when(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winnings")
                .header("Authorization", INVALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized()); 
        verify(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));
    }

    // Test for getLeaderboardByWinRate
    @Test
    public void getLeaderboardByWinRate_validToken_returnsLeaderboard() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        List<LeaderboardEntryDTO> leaderboard = Collections.singletonList(new LeaderboardEntryDTO());
        given(gameHistoryService.getLeaderboardByWinRate()).willReturn(leaderboard);

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winrate")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getLeaderboardByWinRate();
    }

    @Test
    public void getLeaderboardByWinRate_emptyLeaderboard_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        given(gameHistoryService.getLeaderboardByWinRate()).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winrate")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getLeaderboardByWinRate();
    }

    @Test
    public void getLeaderboardByWinRate_invalidToken_throwsUnauthorized() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token from service"))
                .when(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/winrate")
                .header("Authorization", INVALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(INVALID_HEADER_TOKEN));
    }


    // Test for getFriendLeaderboardByWinnings
    @Test
    public void getFriendLeaderboardByWinnings_validToken_returnsFriendLeaderboard() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        List<LeaderboardEntryDTO> leaderboard = Collections.singletonList(new LeaderboardEntryDTO());
        given(gameHistoryService.getFriendLeaderboardByWinnings(mockUserId)).willReturn(leaderboard); 

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winnings")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getFriendLeaderboardByWinnings(mockUserId); 
    }

    @Test
    public void getFriendLeaderboardByWinnings_unauthenticatedUser_throwsUnauthorized() throws Exception {
        // Controller calls userService.getUserByToken with full header token.
        // If user is null, controller's "if (requestingUser == null)" should throw 401.
        given(userService.getUserByToken(eq(UNAUTH_HEADER_TOKEN))).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winnings")
                .header("Authorization", UNAUTH_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(UNAUTH_HEADER_TOKEN));
    }

    @Test
    public void getFriendLeaderboardByWinnings_emptyLeaderboard_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        given(gameHistoryService.getFriendLeaderboardByWinnings(mockUserId)).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winnings")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getFriendLeaderboardByWinnings(mockUserId);
    }

    // Test for getFriendLeaderboardByWinRate
    @Test
    public void getFriendLeaderboardByWinRate_validToken_returnsFriendLeaderboard() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        List<LeaderboardEntryDTO> leaderboard = Collections.singletonList(new LeaderboardEntryDTO());
        given(gameHistoryService.getFriendLeaderboardByWinRate(mockUserId)).willReturn(leaderboard); 

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winrate")
                .header("Authorization", VALID_HEADER_TOKEN) 
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN)); 
        verify(gameHistoryService).getFriendLeaderboardByWinRate(mockUserId); 
    }

    @Test
    public void getFriendLeaderboardByWinRate_unauthenticatedUser_throwsUnauthorized() throws Exception {
        given(userService.getUserByToken(eq(UNAUTH_HEADER_TOKEN))).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winrate")
                .header("Authorization", UNAUTH_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isUnauthorized());
        verify(userService).getUserByToken(eq(UNAUTH_HEADER_TOKEN));
    }

    @Test
    public void getFriendLeaderboardByWinRate_emptyLeaderboard_returnsOkWithEmptyList() throws Exception {
        given(userService.getUserByToken(eq(VALID_HEADER_TOKEN))).willReturn(mockUser);
        given(gameHistoryService.getFriendLeaderboardByWinRate(mockUserId)).willReturn(Collections.emptyList());

        MockHttpServletRequestBuilder getRequest = get("/leaderboard/friends/winrate")
                .header("Authorization", VALID_HEADER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getUserByToken(eq(VALID_HEADER_TOKEN));
        verify(gameHistoryService).getFriendLeaderboardByWinRate(mockUserId);
    }
}
