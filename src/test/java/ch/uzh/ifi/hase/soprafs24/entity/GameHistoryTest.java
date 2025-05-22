package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameHistoryTest {

    private GameHistory gameHistory;

    @BeforeEach
    void setUp() {
        gameHistory = new GameHistory();
        gameHistory.setId(1L);
        gameHistory.setUserId(10L);
        gameHistory.setGameId(100L);
        gameHistory.setPlayedAt(LocalDateTime.now());
        gameHistory.setResult("Win");
        gameHistory.setWinnings(500L);
        List<Long> otherPlayerIds = new ArrayList<>();
        otherPlayerIds.add(20L);
        otherPlayerIds.add(30L);
        gameHistory.setOtherPlayerIds(otherPlayerIds);
    }

    @Test
    void testGetSetId() {
        gameHistory.setId(2L);
        assertEquals(2L, gameHistory.getId());
    }

    @Test
    void testGetSetUserId() {
        gameHistory.setUserId(11L);
        assertEquals(11L, gameHistory.getUserId());
    }

    @Test
    void testGetSetGameId() {
        gameHistory.setGameId(101L);
        assertEquals(101L, gameHistory.getGameId());
    }

    @Test
    void testGetSetPlayedAt() {
        LocalDateTime newPlayedAt = LocalDateTime.now().minusHours(1);
        gameHistory.setPlayedAt(newPlayedAt);
        assertEquals(newPlayedAt, gameHistory.getPlayedAt());
    }

    @Test
    void testGetSetResult() {
        gameHistory.setResult("Loss");
        assertEquals("Loss", gameHistory.getResult());
    }

    @Test
    void testGetSetWinnings() {
        gameHistory.setWinnings(-100L);
        assertEquals(-100L, gameHistory.getWinnings());
    }

    @Test
    void testGetSetOtherPlayerIds() {
        List<Long> newOtherPlayerIds = new ArrayList<>();
        newOtherPlayerIds.add(40L);
        gameHistory.setOtherPlayerIds(newOtherPlayerIds);
        assertEquals(newOtherPlayerIds, gameHistory.getOtherPlayerIds());
        assertEquals(1, gameHistory.getOtherPlayerIds().size());
        assertTrue(gameHistory.getOtherPlayerIds().contains(40L));
    }

    @Test
    void testDefaultConstructor() {
        GameHistory newGameHistory = new GameHistory();
        assertNull(newGameHistory.getId());
        assertNull(newGameHistory.getUserId());
        assertNull(newGameHistory.getGameId());
        assertNull(newGameHistory.getPlayedAt());
        assertNull(newGameHistory.getResult());
        assertNull(newGameHistory.getWinnings());
        assertNull(newGameHistory.getOtherPlayerIds());
    }
}