package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserFriendDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserFriendDTO dto = new UserFriendDTO();
        Date now = new Date();
        Long id = 1L;
        String username = "testuser";
        UserStatus status = UserStatus.ONLINE;
        Date birthday = new Date(now.getTime() - 1000000000L); // A different date for birthday

        // Set values
        dto.setId(id);
        dto.setUsername(username);
        dto.setOnline(status);
        dto.setCreatedAt(now);
        dto.setBirthday(birthday);

        // Assert values using getters
        assertEquals(id, dto.getId(), "ID should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(status, dto.getOnline(), "Online status should match the set value.");
        assertEquals(now, dto.getCreatedAt(), "CreatedAt date should match the set value.");
        assertEquals(birthday, dto.getBirthday(), "Birthday date should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        UserFriendDTO dto = new UserFriendDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getId(), "Default ID should be null.");
        assertNull(dto.getUsername(), "Default username should be null.");
        assertNull(dto.getOnline(), "Default online status should be null.");
        assertNull(dto.getCreatedAt(), "Default createdAt date should be null.");
        assertNull(dto.getBirthday(), "Default birthday date should be null.");
    }

    @Test
    public void testSetNullValues() {
        UserFriendDTO dto = new UserFriendDTO();

        // Set all fields to null (if applicable)
        dto.setId(null);
        dto.setUsername(null);
        dto.setOnline(null);
        dto.setCreatedAt(null);
        dto.setBirthday(null);

        // Assert that getters return null
        assertNull(dto.getId(), "ID should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getOnline(), "Online status should be null after setting to null.");
        assertNull(dto.getCreatedAt(), "CreatedAt date should be null after setting to null.");
        assertNull(dto.getBirthday(), "Birthday date should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        UserFriendDTO dto1 = new UserFriendDTO();
        dto1.setId(1L);
        dto1.setUsername("user1");

        UserFriendDTO dto2 = new UserFriendDTO();
        dto2.setId(2L);
        dto2.setUsername("user2");

        assertEquals(1L, dto1.getId());
        assertEquals("user1", dto1.getUsername());

        assertEquals(2L, dto2.getId());
        assertEquals("user2", dto2.getUsername());
    }
}