package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO.ExperienceLevel; // Assuming ExperienceLevel is part of UserProfileDTO or defined elsewhere accessible
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserPostDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserPostDTO dto = new UserPostDTO();

        String name = "Test User";
        String username = "testpostuser";
        String password = "securePassword123";
        LocalDate birthday = LocalDate.of(1990, 5, 15);
        int profileImage = 3;
        ExperienceLevel experienceLevel = ExperienceLevel.Beginner; // Assuming this enum exists

        // Set values
        dto.setName(name);
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setBirthday(birthday);
        dto.setProfileImage(profileImage);
        dto.setExperienceLevel(experienceLevel);

        // Assert values using getters
        assertEquals(name, dto.getName(), "Name should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(password, dto.getPassword(), "Password should match the set value.");
        assertEquals(birthday, dto.getBirthday(), "Birthday should match the set value.");
        assertEquals(profileImage, dto.getProfileImage(), "ProfileImage should match the set value.");
        assertEquals(experienceLevel, dto.getExperienceLevel(), "ExperienceLevel should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        UserPostDTO dto = new UserPostDTO();

        // Assert default values
        assertNull(dto.getName(), "Default Name should be null.");
        assertNull(dto.getUsername(), "Default Username should be null.");
        assertNull(dto.getPassword(), "Default Password should be null.");
        assertNull(dto.getBirthday(), "Default Birthday should be null.");
        assertEquals(0, dto.getProfileImage(), "Default ProfileImage should be 0 for int.");
        assertNull(dto.getExperienceLevel(), "Default ExperienceLevel should be null.");
    }

    @Test
    public void testSetNullValues() {
        UserPostDTO dto = new UserPostDTO();

        // Set nullable fields to null
        dto.setName(null);
        dto.setUsername(null);
        dto.setPassword(null);
        dto.setBirthday(null);
        // profileImage is int, cannot be null, so we test its default or a specific set value
        dto.setProfileImage(0); // or any other int value
        dto.setExperienceLevel(null);


        // Assert that getters return null for nullable fields
        assertNull(dto.getName(), "Name should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getPassword(), "Password should be null after setting to null.");
        assertNull(dto.getBirthday(), "Birthday should be null after setting to null.");
        assertEquals(0, dto.getProfileImage(), "ProfileImage should retain its set value or default.");
        assertNull(dto.getExperienceLevel(), "ExperienceLevel should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        UserPostDTO dto1 = new UserPostDTO();
        dto1.setUsername("userPost1");
        dto1.setPassword("pass1");
        dto1.setProfileImage(1);

        UserPostDTO dto2 = new UserPostDTO();
        dto2.setUsername("userPost2");
        dto2.setPassword("pass2");
        dto2.setProfileImage(2);

        // Assert values for dto1
        assertEquals("userPost1", dto1.getUsername());
        assertEquals("pass1", dto1.getPassword());
        assertEquals(1, dto1.getProfileImage());

        // Assert values for dto2
        assertEquals("userPost2", dto2.getUsername());
        assertEquals("pass2", dto2.getPassword());
        assertEquals(2, dto2.getProfileImage());

        // Ensure they are different
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
        assertNotEquals(dto1.getPassword(), dto2.getPassword());
        assertNotEquals(dto1.getProfileImage(), dto2.getProfileImage());
    }
}