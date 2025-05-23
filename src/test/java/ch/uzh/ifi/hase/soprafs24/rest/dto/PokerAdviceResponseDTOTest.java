package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PokerAdviceResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        PokerAdviceResponseDTO dto = new PokerAdviceResponseDTO();
        String advice = "You should consider folding with this weak hand.";

        // Set value
        dto.setAdvice(advice);

        // Assert value using getter
        assertEquals(advice, dto.getAdvice(), "Advice should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        PokerAdviceResponseDTO dto = new PokerAdviceResponseDTO();

        // Assert default value (should be null)
        assertNull(dto.getAdvice(), "Default advice should be null.");
    }

    @Test
    public void testSetNullValue() {
        PokerAdviceResponseDTO dto = new PokerAdviceResponseDTO();

        // Set field to null
        dto.setAdvice(null);

        // Assert that getter returns null
        assertNull(dto.getAdvice(), "Advice should be null after setting to null.");
    }
} 