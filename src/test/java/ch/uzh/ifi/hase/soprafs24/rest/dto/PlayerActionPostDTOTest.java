package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PlayerActionPostDTOTest {

    @Test
    public void testGettersAndSetters() {
        PlayerActionPostDTO dto = new PlayerActionPostDTO();

        Long userId = 1L;
        PlayerAction action = PlayerAction.CALL;
        Long amount = 100L;

        // Set values
        dto.setUserId(userId);
        dto.setAction(action);
        dto.setAmount(amount);

        // Assert values using getters
        assertEquals(userId, dto.getUserId(), "UserId should match the set value.");
        assertEquals(action, dto.getAction(), "Action should match the set value.");
        assertEquals(amount, dto.getAmount(), "Amount should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        PlayerActionPostDTO dto = new PlayerActionPostDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getUserId(), "Default UserId should be null.");
        assertNull(dto.getAction(), "Default Action should be null.");
        assertNull(dto.getAmount(), "Default Amount should be null.");
    }

    @Test
    public void testSetNullValues() {
        PlayerActionPostDTO dto = new PlayerActionPostDTO();

        // Set all fields to null
        dto.setUserId(null);
        dto.setAction(null);
        dto.setAmount(null);

        // Assert that getters return null
        assertNull(dto.getUserId(), "UserId should be null after setting to null.");
        assertNull(dto.getAction(), "Action should be null after setting to null.");
        assertNull(dto.getAmount(), "Amount should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        PlayerActionPostDTO dto1 = new PlayerActionPostDTO();
        dto1.setUserId(1L);
        dto1.setAction(PlayerAction.CALL);
        dto1.setAmount(100L);

        PlayerActionPostDTO dto2 = new PlayerActionPostDTO();
        dto2.setUserId(2L);
        dto2.setAction(PlayerAction.FOLD);
        dto2.setAmount(200L);

        // Assert values for dto1
        assertEquals(1L, dto1.getUserId());
        assertEquals(PlayerAction.CALL, dto1.getAction());
        assertEquals(100L, dto1.getAmount());

        // Assert values for dto2
        assertEquals(2L, dto2.getUserId());
        assertEquals(PlayerAction.FOLD, dto2.getAction());
        assertEquals(200L, dto2.getAmount());

        // Ensure they are different
        assertNotEquals(dto1.getUserId(), dto2.getUserId());
        assertNotEquals(dto1.getAction(), dto2.getAction());
        assertNotEquals(dto1.getAmount(), dto2.getAmount());
    }
} 