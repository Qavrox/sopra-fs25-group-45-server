package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStatisticsDTOTest {

    @Test
    public void testGettersAndSetters() {
        GameStatisticsDTO dto = new GameStatisticsDTO();

        double participationRate = 0.85;
        int potsWon = 3;

        // Set values
        dto.setParticipationRate(participationRate);
        dto.setPotsWon(potsWon);

        // Assert values using getters
        assertEquals(participationRate, dto.getParticipationRate(), 0.0001, 
            "ParticipationRate should match the set value.");
        assertEquals(potsWon, dto.getPotsWon(), "PotsWon should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        GameStatisticsDTO dto = new GameStatisticsDTO();

        // Assert default values (should be 0.0 for double and 0 for int)
        assertEquals(0.0, dto.getParticipationRate(), 0.0001, 
            "Default participationRate should be 0.0.");
        assertEquals(0, dto.getPotsWon(), "Default potsWon should be 0.");
    }

    @Test
    public void testDifferentValues() {
        GameStatisticsDTO dto = new GameStatisticsDTO();

        // Test with different values
        double[] participationRates = {0.0, 0.25, 0.5, 0.75, 1.0};
        int[] potsWonValues = {0, 1, 5, 10, 20};

        for (int i = 0; i < participationRates.length; i++) {
            dto.setParticipationRate(participationRates[i]);
            dto.setPotsWon(potsWonValues[i]);

            assertEquals(participationRates[i], dto.getParticipationRate(), 0.0001,
                String.format("ParticipationRate should be %f after setting.", participationRates[i]));
            assertEquals(potsWonValues[i], dto.getPotsWon(),
                String.format("PotsWon should be %d after setting.", potsWonValues[i]));
        }
    }

    @Test
    public void testBoundaryValues() {
        GameStatisticsDTO dto = new GameStatisticsDTO();

        // Test boundary values for participationRate
        dto.setParticipationRate(0.0);
        assertEquals(0.0, dto.getParticipationRate(), 0.0001, 
            "ParticipationRate should handle minimum value (0.0)");

        dto.setParticipationRate(1.0);
        assertEquals(1.0, dto.getParticipationRate(), 0.0001, 
            "ParticipationRate should handle maximum value (1.0)");

        // Test boundary values for potsWon
        dto.setPotsWon(0);
        assertEquals(0, dto.getPotsWon(), 
            "PotsWon should handle minimum value (0)");

        dto.setPotsWon(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getPotsWon(), 
            "PotsWon should handle maximum integer value");
    }
} 