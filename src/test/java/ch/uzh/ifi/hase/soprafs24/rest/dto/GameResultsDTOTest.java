package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game; // Required for Player constructor
import org.junit.jupiter.api.Test;
import java.util.ArrayList; // Required for Player constructor

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
//simport static org.mockito.Mockito.mock;

public class GameResultsDTOTest {

    private Player createMockPlayer() {
        // For DTO tests, often a mock is sufficient, or a minimally initialized real object.
        // Using a real Player object as per other tests' style.
        Game dummyGame = new Game(); // Create a dummy game instance
        dummyGame.setStartCredit(0L); // Set any required fields for Player constructor
        Player player = new Player(1L, new ArrayList<>(), dummyGame); // Assuming constructor
        player.setId(1L); // Set player's own ID
        // Set other relevant Player fields if necessary for the test
        return player;
    }

    private GameStatisticsDTO createMockGameStatisticsDTO() {
        // Mocking GameStatisticsDTO or creating a new instance
        // For simplicity, if GameStatisticsDTO is just a data holder:
        GameStatisticsDTO stats = new GameStatisticsDTO();
        // Set some dummy data if needed for differentiation or specific checks
        stats.setParticipationRate(0.75); // Use actual setter
        stats.setPotsWon(10);             // Use actual setter
        return stats;
    }

    @Test
    public void testGettersAndSetters() {
        GameResultsDTO dto = new GameResultsDTO();

        Player winner = createMockPlayer();
        String winningHand = "Royal Flush";
        GameStatisticsDTO statistics = createMockGameStatisticsDTO();

        // Set values
        dto.setWinner(winner);
        dto.setWinningHand(winningHand);
        dto.setStatistics(statistics);

        // Assert values using getters
        assertEquals(winner, dto.getWinner(), "Winner should match the set value.");
        assertEquals(winningHand, dto.getWinningHand(), "WinningHand should match the set value.");
        assertEquals(statistics, dto.getStatistics(), "Statistics DTO should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        GameResultsDTO dto = new GameResultsDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getWinner(), "Default winner should be null.");
        assertNull(dto.getWinningHand(), "Default winningHand should be null.");
        assertNull(dto.getStatistics(), "Default statistics DTO should be null.");
    }

    @Test
    public void testSetNullValues() {
        GameResultsDTO dto = new GameResultsDTO();

        // Set fields to null
        dto.setWinner(null);
        dto.setWinningHand(null);
        dto.setStatistics(null);

        // Assert that getters return null
        assertNull(dto.getWinner(), "Winner should be null after setting to null.");
        assertNull(dto.getWinningHand(), "WinningHand should be null after setting to null.");
        assertNull(dto.getStatistics(), "Statistics DTO should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        GameResultsDTO dto1 = new GameResultsDTO();
        Player winner1 = createMockPlayer();
        // Modify player1 slightly to ensure it's different if createMockPlayer always returns same base
        winner1.setId(10L); 
        GameStatisticsDTO stats1 = createMockGameStatisticsDTO();
        stats1.setPotsWon(50); // Use actual setter
        dto1.setWinner(winner1);
        dto1.setWinningHand("Straight Flush");
        dto1.setStatistics(stats1);

        GameResultsDTO dto2 = new GameResultsDTO();
        Player winner2 = createMockPlayer();
        winner2.setId(20L); // Ensure player ID is different
        GameStatisticsDTO stats2 = createMockGameStatisticsDTO();
        stats2.setPotsWon(100); // Use actual setter and ensure stats are different
        dto2.setWinner(winner2);
        dto2.setWinningHand("Four of a Kind");
        dto2.setStatistics(stats2);

        // Assert values for dto1
        assertEquals(winner1, dto1.getWinner());
        assertEquals("Straight Flush", dto1.getWinningHand());
        assertEquals(stats1, dto1.getStatistics());

        // Assert values for dto2
        assertEquals(winner2, dto2.getWinner());
        assertEquals("Four of a Kind", dto2.getWinningHand());
        assertEquals(stats2, dto2.getStatistics());

        // Ensure they are different
        assertNotEquals(dto1.getWinner(), dto2.getWinner());
        assertNotEquals(dto1.getWinningHand(), dto2.getWinningHand());
        assertNotEquals(dto1.getStatistics(), dto2.getStatistics());
        // More specific checks for nested objects if necessary
        assertNotEquals(dto1.getWinner().getId(), dto2.getWinner().getId());
        assertNotEquals(dto1.getStatistics().getPotsWon(), dto2.getStatistics().getPotsWon()); // Use actual getter
    }
}