package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserStatisticsDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserStatisticsDTO dto = new UserStatisticsDTO();

        Long userId = 1L;
        String username = "testuser";
        String displayName = "Test User Display";
        Long gamesPlayed = 100L;
        Long wins = 50L;
        Long losses = 40L;
        Double winRate = 0.5;
        Long totalWinnings = 10000L;
        Double averagePosition = 2.5;

        // Set values
        dto.setUserId(userId);
        dto.setUsername(username);
        dto.setDisplayName(displayName);
        dto.setGamesPlayed(gamesPlayed);
        dto.setWins(wins);
        dto.setLosses(losses);
        dto.setWinRate(winRate);
        dto.setTotalWinnings(totalWinnings);
        dto.setAveragePosition(averagePosition);

        // Assert values using getters
        assertEquals(userId, dto.getUserId(), "UserId should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(displayName, dto.getDisplayName(), "DisplayName should match the set value.");
        assertEquals(gamesPlayed, dto.getGamesPlayed(), "GamesPlayed should match the set value.");
        assertEquals(wins, dto.getWins(), "Wins should match the set value.");
        assertEquals(losses, dto.getLosses(), "Losses should match the set value.");
        assertEquals(winRate, dto.getWinRate(), "WinRate should match the set value.");
        assertEquals(totalWinnings, dto.getTotalWinnings(), "TotalWinnings should match the set value.");
        assertEquals(averagePosition, dto.getAveragePosition(), "AveragePosition should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        UserStatisticsDTO dto = new UserStatisticsDTO();

        // Assert default values (should be null for object types, 0 or 0.0 for primitives if not explicitly initialized)
        assertNull(dto.getUserId(), "Default UserId should be null.");
        assertNull(dto.getUsername(), "Default Username should be null.");
        assertNull(dto.getDisplayName(), "Default DisplayName should be null.");
        assertNull(dto.getGamesPlayed(), "Default GamesPlayed should be null (assuming Long).");
        assertNull(dto.getWins(), "Default Wins should be null (assuming Long).");
        assertNull(dto.getLosses(), "Default Losses should be null (assuming Long).");
        assertNull(dto.getWinRate(), "Default WinRate should be null (assuming Double).");
        assertNull(dto.getTotalWinnings(), "Default TotalWinnings should be null (assuming Long).");
        assertNull(dto.getAveragePosition(), "Default AveragePosition should be null (assuming Double).");
    }

    @Test
    public void testSetNullValues() {
        UserStatisticsDTO dto = new UserStatisticsDTO();

        // Set all fields to null
        dto.setUserId(null);
        dto.setUsername(null);
        dto.setDisplayName(null);
        dto.setGamesPlayed(null);
        dto.setWins(null);
        dto.setLosses(null);
        dto.setWinRate(null);
        dto.setTotalWinnings(null);
        dto.setAveragePosition(null);

        // Assert that getters return null
        assertNull(dto.getUserId(), "UserId should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getDisplayName(), "DisplayName should be null after setting to null.");
        assertNull(dto.getGamesPlayed(), "GamesPlayed should be null after setting to null.");
        assertNull(dto.getWins(), "Wins should be null after setting to null.");
        assertNull(dto.getLosses(), "Losses should be null after setting to null.");
        assertNull(dto.getWinRate(), "WinRate should be null after setting to null.");
        assertNull(dto.getTotalWinnings(), "TotalWinnings should be null after setting to null.");
        assertNull(dto.getAveragePosition(), "AveragePosition should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        UserStatisticsDTO dto1 = new UserStatisticsDTO();
        dto1.setUserId(1L);
        dto1.setUsername("user1_stats");
        dto1.setGamesPlayed(10L);

        UserStatisticsDTO dto2 = new UserStatisticsDTO();
        dto2.setUserId(2L);
        dto2.setUsername("user2_stats");
        dto2.setGamesPlayed(20L);

        // Assert values for dto1
        assertEquals(1L, dto1.getUserId());
        assertEquals("user1_stats", dto1.getUsername());
        assertEquals(10L, dto1.getGamesPlayed());

        // Assert values for dto2
        assertEquals(2L, dto2.getUserId());
        assertEquals("user2_stats", dto2.getUsername());
        assertEquals(20L, dto2.getGamesPlayed());

        // Ensure they are different
        assertNotEquals(dto1.getUserId(), dto2.getUserId());
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
    }
}