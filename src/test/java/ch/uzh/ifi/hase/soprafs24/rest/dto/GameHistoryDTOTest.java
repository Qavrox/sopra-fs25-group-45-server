package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GameHistoryDTOTest {

    @Test
    public void testGettersAndSetters() {
        GameHistoryDTO dto = new GameHistoryDTO();

        Long id = 1L;
        Long userId = 10L;
        Long gameId = 100L;
        LocalDateTime playedAt = LocalDateTime.now();
        String result = "Win";
        Long winnings = 500L;
        List<Long> otherPlayerIds = Arrays.asList(20L, 30L);

        // Set values
        dto.setId(id);
        dto.setUserId(userId);
        dto.setGameId(gameId);
        dto.setPlayedAt(playedAt);
        dto.setResult(result);
        dto.setWinnings(winnings);
        dto.setOtherPlayerIds(otherPlayerIds);

        // Assert values using getters
        assertEquals(id, dto.getId(), "ID should match the set value.");
        assertEquals(userId, dto.getUserId(), "UserId should match the set value.");
        assertEquals(gameId, dto.getGameId(), "GameId should match the set value.");
        assertEquals(playedAt, dto.getPlayedAt(), "PlayedAt should match the set value.");
        assertEquals(result, dto.getResult(), "Result should match the set value.");
        assertEquals(winnings, dto.getWinnings(), "Winnings should match the set value.");
        assertEquals(otherPlayerIds, dto.getOtherPlayerIds(), "OtherPlayerIds should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        GameHistoryDTO dto = new GameHistoryDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getId(), "Default ID should be null.");
        assertNull(dto.getUserId(), "Default UserId should be null.");
        assertNull(dto.getGameId(), "Default GameId should be null.");
        assertNull(dto.getPlayedAt(), "Default PlayedAt should be null.");
        assertNull(dto.getResult(), "Default Result should be null.");
        assertNull(dto.getWinnings(), "Default Winnings should be null.");
        assertNull(dto.getOtherPlayerIds(), "Default OtherPlayerIds should be null.");
    }

    @Test
    public void testSetNullValues() {
        GameHistoryDTO dto = new GameHistoryDTO();

        // Set all fields to null
        dto.setId(null);
        dto.setUserId(null);
        dto.setGameId(null);
        dto.setPlayedAt(null);
        dto.setResult(null);
        dto.setWinnings(null);
        dto.setOtherPlayerIds(null);

        // Assert that getters return null
        assertNull(dto.getId(), "ID should be null after setting to null.");
        assertNull(dto.getUserId(), "UserId should be null after setting to null.");
        assertNull(dto.getGameId(), "GameId should be null after setting to null.");
        assertNull(dto.getPlayedAt(), "PlayedAt should be null after setting to null.");
        assertNull(dto.getResult(), "Result should be null after setting to null.");
        assertNull(dto.getWinnings(), "Winnings should be null after setting to null.");
        assertNull(dto.getOtherPlayerIds(), "OtherPlayerIds should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        GameHistoryDTO dto1 = new GameHistoryDTO();
        dto1.setId(1L);
        dto1.setUserId(10L);
        dto1.setResult("Win");

        GameHistoryDTO dto2 = new GameHistoryDTO();
        dto2.setId(2L);
        dto2.setUserId(20L);
        dto2.setResult("Loss");

        // Assert values for dto1
        assertEquals(1L, dto1.getId());
        assertEquals(10L, dto1.getUserId());
        assertEquals("Win", dto1.getResult());

        // Assert values for dto2
        assertEquals(2L, dto2.getId());
        assertEquals(20L, dto2.getUserId());
        assertEquals("Loss", dto2.getResult());

        // Ensure they are different
        assertNotEquals(dto1.getId(), dto2.getId());
        assertNotEquals(dto1.getUserId(), dto2.getUserId());
        assertNotEquals(dto1.getResult(), dto2.getResult());
    }

    @Test
    public void testListHandling() {
        GameHistoryDTO dto = new GameHistoryDTO();
        
        List<Long> otherPlayerIds = new ArrayList<>();
        otherPlayerIds.add(100L);
        otherPlayerIds.add(200L);
        
        dto.setOtherPlayerIds(otherPlayerIds);
        assertEquals(otherPlayerIds, dto.getOtherPlayerIds(), "OtherPlayerIds list should match.");
        assertEquals(2, dto.getOtherPlayerIds().size(), "Size of OtherPlayerIds list should be 2.");

        List<Long> emptyList = new ArrayList<>();
        dto.setOtherPlayerIds(emptyList);
        assertEquals(emptyList, dto.getOtherPlayerIds(), "OtherPlayerIds list should be empty.");
        assertEquals(0, dto.getOtherPlayerIds().size(), "Size of OtherPlayerIds list should be 0 after setting to empty list.");

        dto.setOtherPlayerIds(null);
        assertNull(dto.getOtherPlayerIds(), "OtherPlayerIds list should be null after setting to null.");
    }
}