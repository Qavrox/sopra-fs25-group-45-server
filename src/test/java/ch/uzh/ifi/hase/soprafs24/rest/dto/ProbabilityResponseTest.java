package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProbabilityResponseTest {

    @Test
    public void testGettersAndSetters() {
        ProbabilityResponse response = new ProbabilityResponse();
        double probability = 0.75;

        // Set value
        response.setProbability(probability);

        // Assert value using getter
        assertEquals(probability, response.getProbability(), 0.0001, "Probability should match the set value.");
    }

    @Test
    public void testDefaultValue() {
        ProbabilityResponse response = new ProbabilityResponse();

        // Assert default value (should be 0.0 for primitive double)
        assertEquals(0.0, response.getProbability(), 0.0001, "Default probability should be 0.0.");
    }

    @Test
    public void testDifferentValues() {
        ProbabilityResponse response = new ProbabilityResponse();

        // Test with different probability values
        double[] testValues = {0.0, 0.25, 0.5, 0.75, 1.0};
        
        for (double value : testValues) {
            response.setProbability(value);
            assertEquals(value, response.getProbability(), 0.0001, 
                String.format("Probability should be %f after setting.", value));
        }
    }

    @Test
    public void testBoundaryValues() {
        ProbabilityResponse response = new ProbabilityResponse();

        // Test boundary values
        response.setProbability(0.0);
        assertEquals(0.0, response.getProbability(), 0.0001, 
            "Probability should handle minimum value (0.0)");

        response.setProbability(1.0);
        assertEquals(1.0, response.getProbability(), 0.0001, 
            "Probability should handle maximum value (1.0)");

        // Test values outside normal probability range
        response.setProbability(-0.1);
        assertEquals(-0.1, response.getProbability(), 0.0001, 
            "Probability should handle negative values");

        response.setProbability(1.1);
        assertEquals(1.1, response.getProbability(), 0.0001, 
            "Probability should handle values greater than 1.0");

        // Test extreme values
        response.setProbability(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, response.getProbability(), 0.0001, 
            "Probability should handle minimum double value");

        response.setProbability(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, response.getProbability(), 0.0001, 
            "Probability should handle maximum double value");
    }
} 