package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserGetDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserGetDTO dto = new UserGetDTO();

        Long id = 1L;
        String name = "Test User";
        String username = "testuser";
        UserLevel experienceLevel = UserLevel.Beginner;
        boolean online = true;
        LocalDate creationDate = LocalDate.now();
        LocalDate birthday = LocalDate.of(1990, 1, 1);
        int profileImage = 1;

        dto.setId(id);
        dto.setName(name);
        dto.setUsername(username);
        dto.setexperienceLevel(experienceLevel);
        dto.setOnline(online);
        dto.setCreationDate(creationDate);
        dto.setBirthday(birthday);
        dto.setProfileImage(profileImage);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(username, dto.getUsername());
        assertEquals(experienceLevel, dto.getexperienceLevel());
        assertEquals(online, dto.getOnline());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(birthday, dto.getBirthday());
        assertEquals(profileImage, dto.getProfileImage());
    }

    @Test
    public void testDefaultValues() {
        UserGetDTO dto = new UserGetDTO();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getUsername());
        assertNull(dto.getexperienceLevel());
        assertFalse(dto.getOnline());
        assertNull(dto.getCreationDate());
        assertNull(dto.getBirthday());
        assertEquals(0, dto.getProfileImage());
    }

    @Test
    public void testDifferentInstances() {
        UserGetDTO dto1 = new UserGetDTO();
        dto1.setId(1L);
        dto1.setUsername("user1");
        dto1.setProfileImage(1);
        dto1.setOnline(true);

        UserGetDTO dto2 = new UserGetDTO();
        dto2.setId(2L);
        dto2.setUsername("user2");
        dto2.setProfileImage(2);
        dto2.setOnline(false);

        // Assert values for dto1
        assertEquals(1L, dto1.getId());
        assertEquals("user1", dto1.getUsername());
        assertEquals(1, dto1.getProfileImage());
        assertTrue(dto1.getOnline());

        // Assert values for dto2
        assertEquals(2L, dto2.getId());
        assertEquals("user2", dto2.getUsername());
        assertEquals(2, dto2.getProfileImage());
        assertFalse(dto2.getOnline());

        // Ensure they are different
        assertNotEquals(dto1.getId(), dto2.getId());
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
        assertNotEquals(dto1.getProfileImage(), dto2.getProfileImage());
        assertNotEquals(dto1.getOnline(), dto2.getOnline());
    }
}