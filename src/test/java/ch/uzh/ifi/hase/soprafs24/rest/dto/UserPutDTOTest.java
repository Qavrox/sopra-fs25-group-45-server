package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserPutDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserPutDTO dto = new UserPutDTO();

        String name = "Test User";
        String username = "testuser";
        UserLevel experienceLevel = UserLevel.Beginner;
        String password = "password123";
        LocalDate birthday = LocalDate.of(1990, 1, 1);
        int profileImage = 1;

        // Set values
        dto.setName(name);
        dto.setUsername(username);
        dto.setexperienceLevel(experienceLevel);
        dto.setPassword(password);
        dto.setBirthday(birthday);
        dto.setProfileImage(profileImage);

        // Assert values using getters
        assertEquals(name, dto.getName(), "Name should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(experienceLevel, dto.getexperienceLevel(), "ExperienceLevel should match the set value.");
        assertEquals(password, dto.getPassword(), "Password should match the set value.");
        assertEquals(birthday, dto.getBirthday(), "Birthday should match the set value.");
        assertEquals(profileImage, dto.getProfileImage(), "ProfileImage should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        UserPutDTO dto = new UserPutDTO();

        // Assert default values
        assertNull(dto.getName(), "Default name should be null.");
        assertNull(dto.getUsername(), "Default username should be null.");
        assertNull(dto.getexperienceLevel(), "Default experienceLevel should be null.");
        assertNull(dto.getPassword(), "Default password should be null.");
        assertNull(dto.getBirthday(), "Default birthday should be null.");
        assertEquals(0, dto.getProfileImage(), "Default profileImage should be 0.");
    }

    @Test
    public void testSetNullValues() {
        UserPutDTO dto = new UserPutDTO();

        // Set nullable fields to null
        dto.setName(null);
        dto.setUsername(null);
        dto.setexperienceLevel(null);
        dto.setPassword(null);
        dto.setBirthday(null);
        // profileImage is int, cannot be null

        // Assert that getters return null for nullable fields
        assertNull(dto.getName(), "Name should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getexperienceLevel(), "ExperienceLevel should be null after setting to null.");
        assertNull(dto.getPassword(), "Password should be null after setting to null.");
        assertNull(dto.getBirthday(), "Birthday should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        UserPutDTO dto1 = new UserPutDTO();
        dto1.setName("User 1");
        dto1.setUsername("user1");
        dto1.setexperienceLevel(UserLevel.Beginner);

        UserPutDTO dto2 = new UserPutDTO();
        dto2.setName("User 2");
        dto2.setUsername("user2");
        dto2.setexperienceLevel(UserLevel.Expert);

        // Assert values for dto1
        assertEquals("User 1", dto1.getName());
        assertEquals("user1", dto1.getUsername());
        assertEquals(UserLevel.Beginner, dto1.getexperienceLevel());

        // Assert values for dto2
        assertEquals("User 2", dto2.getName());
        assertEquals("user2", dto2.getUsername());
        assertEquals(UserLevel.Expert, dto2.getexperienceLevel());

        // Ensure they are different
        assertNotEquals(dto1.getName(), dto2.getName());
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
        assertNotEquals(dto1.getexperienceLevel(), dto2.getexperienceLevel());
    }
} 