package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameHistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserStatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GameHistoryServiceTest {

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFriendsService userFriendsService;

    @InjectMocks
    private GameHistoryService gameHistoryService;

    private User testUser;
    private GameHistory testGameHistory1;
    private GameHistory testGameHistory2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test User");
        testUser.setGamesPlayed(0L);
        testUser.setGamesWon(0L);
        testUser.setTotalWinnings(0L);
        testUser.setWinRate(0.0);

        testGameHistory1 = new GameHistory();
        testGameHistory1.setId(101L);
        testGameHistory1.setUserId(1L);
        testGameHistory1.setGameId(1L);
        testGameHistory1.setResult("Win");
        testGameHistory1.setWinnings(100L);
        testGameHistory1.setPlayedAt(LocalDateTime.now().minusDays(1));
        testGameHistory1.setOtherPlayerIds(Arrays.asList(2L, 3L));

        testGameHistory2 = new GameHistory();
        testGameHistory2.setId(102L);
        testGameHistory2.setUserId(1L);
        testGameHistory2.setGameId(2L);
        testGameHistory2.setResult("Loss");
        testGameHistory2.setWinnings(-50L);
        testGameHistory2.setPlayedAt(LocalDateTime.now());
        testGameHistory2.setOtherPlayerIds(Arrays.asList(4L, 5L));
    }

    @Test
    void recordGameResult_validInput_success() {
        when(userRepository.findByid(1L)).thenReturn(testUser);
        when(gameHistoryRepository.save(any(GameHistory.class))).thenAnswer(invocation -> {
            GameHistory gh = invocation.getArgument(0);
            gh.setId(103L); // Simulate saving and getting an ID
            return gh;
        });
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        GameHistory result = gameHistoryService.recordGameResult(1L, 3L, "Win", 200L, Arrays.asList(6L, 7L));

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(3L, result.getGameId());
        assertEquals("Win", result.getResult());
        assertEquals(200L, result.getWinnings());
        assertNotNull(result.getPlayedAt());

        // Verify user stats updated
        assertEquals(1L, testUser.getGamesPlayed());
        assertEquals(1L, testUser.getGamesWon());
        assertEquals(200L, testUser.getTotalWinnings());
        assertEquals(100.0, testUser.getWinRate());

        verify(userRepository).findByid(1L);
        verify(gameHistoryRepository).save(any(GameHistory.class));
        verify(userRepository).save(testUser);
    }

    @Test
    void recordGameResult_loss_updatesStatsCorrectly() {
        testUser.setGamesPlayed(1L);
        testUser.setGamesWon(1L);
        testUser.setTotalWinnings(100L);
        testUser.updateWinRate(); // Should be 100.0

        when(userRepository.findByid(1L)).thenReturn(testUser);
        when(gameHistoryRepository.save(any(GameHistory.class))).thenReturn(new GameHistory());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        gameHistoryService.recordGameResult(1L, 3L, "Loss", -50L, Arrays.asList(6L, 7L));

        assertEquals(2L, testUser.getGamesPlayed());
        assertEquals(1L, testUser.getGamesWon()); // Wins should not change
        assertEquals(50L, testUser.getTotalWinnings()); // 100 - 50
        assertEquals(50.0, testUser.getWinRate()); // 1 win / 2 games
    }


    @Test
    void recordGameResult_nullUserId_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameHistoryService.recordGameResult(null, 1L, "Win", 100L, Collections.emptyList());
        });
        assertEquals("All required fields must be provided", exception.getMessage());
    }

    @Test
    void getUserGameHistory_userExists_returnsHistory() {
        when(gameHistoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(testGameHistory1, testGameHistory2));

        List<GameHistory> histories = gameHistoryService.getUserGameHistory(1L);

        assertNotNull(histories);
        assertEquals(2, histories.size());
        verify(gameHistoryRepository).findByUserId(1L);
    }

    @Test
    void getUserGameHistoryByTimeRange_userExists_returnsFilteredHistory() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().minusHours(12); // Assuming testGameHistory1 is within, testGameHistory2 is not

        when(gameHistoryRepository.findByUserIdAndPlayedAtBetween(1L, startDate, endDate))
                .thenReturn(Collections.singletonList(testGameHistory1));

        List<GameHistory> histories = gameHistoryService.getUserGameHistoryByTimeRange(1L, startDate, endDate);

        assertNotNull(histories);
        assertEquals(1, histories.size());
        assertEquals(testGameHistory1.getId(), histories.get(0).getId());
        verify(gameHistoryRepository).findByUserIdAndPlayedAtBetween(1L, startDate, endDate);
    }

    @Test
    void getUserStatistics_userExists_returnsStatistics() {
        when(userRepository.findByid(1L)).thenReturn(testUser);
        when(gameHistoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(testGameHistory1, testGameHistory2));

        UserStatisticsDTO stats = gameHistoryService.getUserStatistics(1L, null, null);

        assertNotNull(stats);
        assertEquals(1L, stats.getUserId());
        assertEquals(testUser.getUsername(), stats.getUsername());
        assertEquals(2, stats.getGamesPlayed());
        assertEquals(1, stats.getWins());
        assertEquals(1, stats.getLosses());
        assertEquals(50.0, stats.getWinRate());
        assertEquals(50L, stats.getTotalWinnings()); // 100 - 50

        verify(userRepository).findByid(1L);
        verify(gameHistoryRepository).findByUserId(1L);
    }

    @Test
    void getUserStatistics_withTimeRange_returnsFilteredStatistics() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().minusHours(12);
        when(userRepository.findByid(1L)).thenReturn(testUser);
        when(gameHistoryRepository.findByUserIdAndPlayedAtBetween(1L, startDate, endDate))
                .thenReturn(Collections.singletonList(testGameHistory1));

        UserStatisticsDTO stats = gameHistoryService.getUserStatistics(1L, startDate, endDate);

        assertNotNull(stats);
        assertEquals(1, stats.getGamesPlayed());
        assertEquals(1, stats.getWins());
        assertEquals(0, stats.getLosses());
        assertEquals(100.0, stats.getWinRate());
        assertEquals(100L, stats.getTotalWinnings());
    }

    @Test
    void getUserStatistics_userNotFound_throwsIllegalArgumentException() {
        when(userRepository.findByid(anyLong())).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameHistoryService.getUserStatistics(99L, null, null);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getLeaderboardByWinnings_returnsSortedLeaderboard() {
        User user2 = new User();
        user2.setId(2L); user2.setUsername("user2"); user2.setName("User Two");
        user2.setTotalWinnings(500L); user2.setWinRate(60.0); user2.setGamesPlayed(10L);

        testUser.setTotalWinnings(1000L); testUser.setWinRate(70.0); testUser.setGamesPlayed(20L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<LeaderboardEntryDTO> leaderboard = gameHistoryService.getLeaderboardByWinnings();

        assertNotNull(leaderboard);
        assertEquals(2, leaderboard.size());
        assertEquals(testUser.getId(), leaderboard.get(0).getUserId()); // testUser has higher winnings
        assertEquals(user2.getId(), leaderboard.get(1).getUserId());
        assertEquals(1, leaderboard.get(0).getRank());
        assertEquals(2, leaderboard.get(1).getRank());
    }

    @Test
    void getLeaderboardByWinRate_returnsSortedLeaderboard_filtersNoPlayUsers() {
        User user2 = new User(); // Played games
        user2.setId(2L); user2.setUsername("user2"); user2.setName("User Two");
        user2.setTotalWinnings(500L); user2.setWinRate(60.0); user2.setGamesPlayed(10L);

        testUser.setTotalWinnings(1000L); testUser.setWinRate(70.0); testUser.setGamesPlayed(20L); // Played games

        User user3 = new User(); // No games played
        user3.setId(3L); user3.setUsername("user3"); user3.setName("User Three");
        user3.setTotalWinnings(0L); user3.setWinRate(0.0); user3.setGamesPlayed(0L);

        when(userRepository.findByGamesPlayedGreaterThanEqual(1L)).thenReturn(Arrays.asList(testUser, user2));

        List<LeaderboardEntryDTO> leaderboard = gameHistoryService.getLeaderboardByWinRate();

        assertNotNull(leaderboard);
        assertEquals(2, leaderboard.size()); // User3 should be filtered out
        assertEquals(testUser.getId(), leaderboard.get(0).getUserId()); // testUser has higher win rate
        assertEquals(user2.getId(), leaderboard.get(1).getUserId());
        assertEquals(1, leaderboard.get(0).getRank());
        assertEquals(2, leaderboard.get(1).getRank());
        verify(userRepository).findByGamesPlayedGreaterThanEqual(1L);
    }

    @Test
    void getFriendLeaderboardByWinnings_currentUserNotFound_throwsException() {
        when(userRepository.findByid(anyLong())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> gameHistoryService.getFriendLeaderboardByWinnings(1L));
    }

    @Test
    void getFriendLeaderboardByWinnings_success() {
        User friendUser = new User();
        friendUser.setId(2L); friendUser.setUsername("friend"); friendUser.setName("Friend User");
        friendUser.setTotalWinnings(500L); friendUser.setWinRate(60.0); friendUser.setGamesPlayed(10L);

        testUser.setTotalWinnings(1000L); // Current user

        when(userRepository.findByid(1L)).thenReturn(testUser);
        when(userFriendsService.getFriends(1L)).thenReturn(new ArrayList<>(Collections.singletonList(friendUser)));

        List<LeaderboardEntryDTO> leaderboard = gameHistoryService.getFriendLeaderboardByWinnings(1L);

        assertNotNull(leaderboard);
        assertEquals(2, leaderboard.size()); // Current user + 1 friend
        assertEquals(testUser.getId(), leaderboard.get(0).getUserId()); // Current user has higher winnings
        assertEquals(friendUser.getId(), leaderboard.get(1).getUserId());
        assertEquals(1, leaderboard.get(0).getRank());
        assertEquals(2, leaderboard.get(1).getRank());
    }

    @Test
    void getFriendLeaderboardByWinRate_success_filtersAndSorts() {
        User friend1 = new User(); // High win rate, played games
        friend1.setId(2L); friend1.setUsername("friend1"); friend1.setName("Friend One");
        friend1.setTotalWinnings(500L); friend1.setWinRate(80.0); friend1.setGamesPlayed(10L);

        User friend2 = new User(); // Low win rate, played games
        friend2.setId(3L); friend2.setUsername("friend2"); friend2.setName("Friend Two");
        friend2.setTotalWinnings(100L); friend2.setWinRate(30.0); friend2.setGamesPlayed(5L);

        User friend3 = new User(); // No games played
        friend3.setId(4L); friend3.setUsername("friend3"); friend3.setName("Friend Three");
        friend3.setTotalWinnings(0L); friend3.setWinRate(0.0); friend3.setGamesPlayed(0L);

        testUser.setWinRate(70.0); testUser.setGamesPlayed(20L); // Current user

        when(userRepository.findByid(1L)).thenReturn(testUser);
        // Ensure UserFriendsService.getFriends returns a modifiable list
        when(userFriendsService.getFriends(1L)).thenReturn(new ArrayList<>(Arrays.asList(friend1, friend2, friend3)));

        List<LeaderboardEntryDTO> leaderboard = gameHistoryService.getFriendLeaderboardByWinRate(1L);

        assertNotNull(leaderboard);
        assertEquals(3, leaderboard.size()); // testUser, friend1, friend2 (friend3 filtered)
        assertEquals(friend1.getId(), leaderboard.get(0).getUserId()); // friend1 has highest win rate (80%)
        assertEquals(testUser.getId(), leaderboard.get(1).getUserId()); // then testUser (70%)
        assertEquals(friend2.getId(), leaderboard.get(2).getUserId()); // then friend2 (30%)
        assertEquals(1, leaderboard.get(0).getRank());
        assertEquals(2, leaderboard.get(1).getRank());
        assertEquals(3, leaderboard.get(2).getRank());
    }
} 