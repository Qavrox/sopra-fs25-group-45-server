package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GameCreationPostDTOTest {

    @Test
    public void testGettersAndSetters() {
        GameCreationPostDTO dto = new GameCreationPostDTO();

        Long creatorId = 1L;
        String password = "gamePassword123";
        boolean isPublic = true;
        int maximalPlayers = 6;
        int startCredit = 2000;
        int smallBlind = 25;
        int bigBlind = 50;

        // Set values
        dto.setCreatorId(creatorId);
        dto.setPassword(password);
        dto.setMaximalPlayers(maximalPlayers);
        dto.setStartCredit(startCredit);
        dto.setSmallBlind(smallBlind);
        dto.setBigBlind(bigBlind);

        // Assert values using getters
        assertEquals(creatorId, dto.getCreatorId(), "CreatorId should match the set value.");
        assertEquals(password, dto.getPassword(), "Password should match the set value.");
        assertEquals(isPublic, dto.getIsPublic(), "isPublic flag should match the set value.");
        assertEquals(maximalPlayers, dto.getMaximalPlayers(), "MaximalPlayers should match the set value.");
        assertEquals(startCredit, dto.getStartCredit(), "StartCredit should match the set value.");
        assertEquals(smallBlind, dto.getSmallBlind(), "SmallBlind should match the set value.");
        assertEquals(bigBlind, dto.getBigBlind(), "BigBlind should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        GameCreationPostDTO dto = new GameCreationPostDTO();

        // Assert default values
        assertNull(dto.getCreatorId(), "Default CreatorId should be null.");
        assertNull(dto.getPassword(), "Default Password should be null.");
        assertFalse(dto.getIsPublic(), "Default isPublic should be false.");
        assertEquals(0, dto.getMaximalPlayers(), "Default MaximalPlayers should be 0 for int.");
        assertEquals(0, dto.getStartCredit(), "Default StartCredit should be 0 for int.");
        assertEquals(0, dto.getSmallBlind(), "Default SmallBlind should be 0 for int.");
        assertEquals(0, dto.getBigBlind(), "Default BigBlind should be 0 for int.");
    }

    @Test
    public void testSetNullValuesForNullableFields() {
        GameCreationPostDTO dto = new GameCreationPostDTO();

        // Set nullable fields to null
        dto.setCreatorId(null);
        dto.setPassword(null);

        // Assert that getters return null for nullable fields
        assertNull(dto.getCreatorId(), "CreatorId should be null after setting to null.");
        assertNull(dto.getPassword(), "Password should be null after setting to null.");
    }

    @Test
    public void testBooleanIsPublicSetterAndGetter() {
        GameCreationPostDTO dto = new GameCreationPostDTO();

        dto.setPublic(true);
        assertTrue(dto.getIsPublic(), "isPublic should be true after setting to true.");

        dto.setPublic(false);
        assertFalse(dto.getIsPublic(), "isPublic should be false after setting to false.");
    }


    @Test
    public void testDifferentInstances() {
        GameCreationPostDTO dto1 = new GameCreationPostDTO();
        dto1.setCreatorId(10L);
        dto1.setPassword("pass1");
        dto1.setMaximalPlayers(4);
        dto1.setPublic(true);

        GameCreationPostDTO dto2 = new GameCreationPostDTO();
        dto2.setCreatorId(20L);
        dto2.setPassword("pass2");
        dto2.setMaximalPlayers(8);
        dto2.setPublic(false);

        // Assert values for dto1
        assertEquals(10L, dto1.getCreatorId());
        assertEquals("pass1", dto1.getPassword());
        assertEquals(4, dto1.getMaximalPlayers());
        assertTrue(dto1.getIsPublic());

        // Assert values for dto2
        assertEquals(20L, dto2.getCreatorId());
        assertEquals("pass2", dto2.getPassword());
        assertEquals(8, dto2.getMaximalPlayers());
        assertFalse(dto2.getIsPublic());

        // Ensure they are different
        assertNotEquals(dto1.getCreatorId(), dto2.getCreatorId());
        assertNotEquals(dto1.getPassword(), dto2.getPassword());
        assertNotEquals(dto1.getMaximalPlayers(), dto2.getMaximalPlayers());
        assertNotEquals(dto1.getIsPublic(), dto2.getIsPublic());
    }
}