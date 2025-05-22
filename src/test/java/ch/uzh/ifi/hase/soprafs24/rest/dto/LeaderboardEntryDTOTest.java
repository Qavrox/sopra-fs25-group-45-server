package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LeaderboardEntryDTOTest {

    @Test
    public void testGettersAndSetters() {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();

        Long userId = 1L;
        String username = "testUser";
        String displayName = "Test User";
        Double winRate = 0.75;
        Long totalWinnings = 1000L;
        Long gamesPlayed = 100L;
        Long rank = 1L;

        // Set values
        dto.setUserId(userId);
        dto.setUsername(username);
        dto.setDisplayName(displayName);
        dto.setWinRate(winRate);
        dto.setTotalWinnings(totalWinnings);
        dto.setGamesPlayed(gamesPlayed);
        dto.setRank(rank);

        // Assert values using getters
        assertEquals(userId, dto.getUserId(), "UserId should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(displayName, dto.getDisplayName(), "DisplayName should match the set value.");
        assertEquals(winRate, dto.getWinRate(), "WinRate should match the set value.");
        assertEquals(totalWinnings, dto.getTotalWinnings(), "TotalWinnings should match the set value.");
        assertEquals(gamesPlayed, dto.getGamesPlayed(), "GamesPlayed should match the set value.");
        assertEquals(rank, dto.getRank(), "Rank should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getUserId(), "Default UserId should be null.");
        assertNull(dto.getUsername(), "Default Username should be null.");
        assertNull(dto.getDisplayName(), "Default DisplayName should be null.");
        assertNull(dto.getWinRate(), "Default WinRate should be null.");
        assertNull(dto.getTotalWinnings(), "Default TotalWinnings should be null.");
        assertNull(dto.getGamesPlayed(), "Default GamesPlayed should be null.");
        assertNull(dto.getRank(), "Default Rank should be null.");
    }

    @Test
    public void testSetNullValues() {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();

        // Set all fields to null
        dto.setUserId(null);
        dto.setUsername(null);
        dto.setDisplayName(null);
        dto.setWinRate(null);
        dto.setTotalWinnings(null);
        dto.setGamesPlayed(null);
        dto.setRank(null);

        // Assert that getters return null
        assertNull(dto.getUserId(), "UserId should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getDisplayName(), "DisplayName should be null after setting to null.");
        assertNull(dto.getWinRate(), "WinRate should be null after setting to null.");
        assertNull(dto.getTotalWinnings(), "TotalWinnings should be null after setting to null.");
        assertNull(dto.getGamesPlayed(), "GamesPlayed should be null after setting to null.");
        assertNull(dto.getRank(), "Rank should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        LeaderboardEntryDTO dto1 = new LeaderboardEntryDTO();
        dto1.setUserId(1L);
        dto1.setUsername("user1");
        dto1.setRank(1L);

        LeaderboardEntryDTO dto2 = new LeaderboardEntryDTO();
        dto2.setUserId(2L);
        dto2.setUsername("user2");
        dto2.setRank(2L);

        // Assert values for dto1
        assertEquals(1L, dto1.getUserId());
        assertEquals("user1", dto1.getUsername());
        assertEquals(1L, dto1.getRank());

        // Assert values for dto2
        assertEquals(2L, dto2.getUserId());
        assertEquals("user2", dto2.getUsername());
        assertEquals(2L, dto2.getRank());

        // Ensure they are different
        assertNotEquals(dto1.getUserId(), dto2.getUserId());
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
        assertNotEquals(dto1.getRank(), dto2.getRank());
    }
} 