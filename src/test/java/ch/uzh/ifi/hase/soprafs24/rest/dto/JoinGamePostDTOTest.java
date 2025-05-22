package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JoinGamePostDTOTest {

    @Test
    public void testGettersAndSetters() {
        JoinGamePostDTO dto = new JoinGamePostDTO();
        String password = "testPassword123";

        // Set value
        dto.setPassword(password);

        // Assert value using getter
        assertEquals(password, dto.getPassword(), "Password should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        JoinGamePostDTO dto = new JoinGamePostDTO();

        // Assert default value (should be null)
        assertNull(dto.getPassword(), "Default password should be null.");
    }

    @Test
    public void testSetNullValue() {
        JoinGamePostDTO dto = new JoinGamePostDTO();

        // Set field to null
        dto.setPassword(null);

        // Assert that getter returns null
        assertNull(dto.getPassword(), "Password should be null after setting to null.");
    }
} 