package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
// Assuming ExperienceLevel is an enum. Adjust import if it's defined elsewhere,
// e.g., ch.uzh.ifi.hase.soprafs24.constant.ExperienceLevel
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO.ExperienceLevel;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserProfileDTOTest {

    @Test
    public void testGettersAndSetters() {
        UserProfileDTO dto = new UserProfileDTO();

        Long id = 1L;
        String username = "testProfileUser";
        String displayName = "Test Profile Display";
        int avatarUrl = 5; // Assuming int, if String, change type
        ExperienceLevel experienceLevel = ExperienceLevel.Intermediate; // Make sure this enum and value exist
        LocalDate birthday = LocalDate.of(1985, 10, 20);
        LocalDateTime createdAt = LocalDateTime.now().minusHours(5);
        boolean isOnline = true;

        // Set values
        dto.setId(id);
        dto.setUsername(username);
        dto.setDisplayName(displayName);
        dto.setAvatarUrl(avatarUrl);
        dto.setExperienceLevel(experienceLevel);
        dto.setBirthday(birthday);
        dto.setCreatedAt(createdAt);
        dto.setOnline(isOnline); // Assuming setter is setOnline

        // Assert values using getters
        assertEquals(id, dto.getId(), "ID should match the set value.");
        assertEquals(username, dto.getUsername(), "Username should match the set value.");
        assertEquals(displayName, dto.getDisplayName(), "DisplayName should match the set value.");
        assertEquals(avatarUrl, dto.getAvatarUrl(), "AvatarUrl should match the set value.");
        assertEquals(experienceLevel, dto.getExperienceLevel(), "ExperienceLevel should match the set value.");
        assertEquals(birthday, dto.getBirthday(), "Birthday should match the set value.");
        assertEquals(createdAt, dto.getCreatedAt(), "CreatedAt should match the set value.");
        // Assuming getter is isOnline() or getOnline() - standard for boolean is isOnline()
        assertEquals(isOnline, dto.isOnline(), "Online status should match the set value.");
    }

    @Test
    public void testDefaultValues() {
        UserProfileDTO dto = new UserProfileDTO();

        // Assert default values
        assertNull(dto.getId(), "Default ID should be null.");
        assertNull(dto.getUsername(), "Default Username should be null.");
        assertNull(dto.getDisplayName(), "Default DisplayName should be null.");
        assertEquals(0, dto.getAvatarUrl(), "Default AvatarUrl should be 0 for int.");
        assertNull(dto.getExperienceLevel(), "Default ExperienceLevel should be null.");
        assertNull(dto.getBirthday(), "Default Birthday should be null.");
        assertNull(dto.getCreatedAt(), "Default CreatedAt should be null.");
        // Assuming 'online' is a boolean primitive, its default is false.
        // If it's a Boolean object, its default would be null.
        assertFalse(dto.isOnline(), "Default online status should be false.");
    }

    @Test
    public void testSetNullValues() {
        UserProfileDTO dto = new UserProfileDTO();

        // Set nullable fields to null
        dto.setId(null);
        dto.setUsername(null);
        dto.setDisplayName(null);
        // avatarUrl is int, cannot be null
        dto.setExperienceLevel(null);
        dto.setBirthday(null);
        dto.setCreatedAt(null);
        // online is boolean, cannot be null

        // Assert that getters return null for nullable fields
        assertNull(dto.getId(), "ID should be null after setting to null.");
        assertNull(dto.getUsername(), "Username should be null after setting to null.");
        assertNull(dto.getDisplayName(), "DisplayName should be null after setting to null.");
        assertNull(dto.getExperienceLevel(), "ExperienceLevel should be null after setting to null.");
        assertNull(dto.getBirthday(), "Birthday should be null after setting to null.");
        assertNull(dto.getCreatedAt(), "CreatedAt should be null after setting to null.");
    }

    @Test
    public void testBooleanOnlineSetterAndGetter() {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setOnline(true);
        assertTrue(dto.isOnline(), "Online status should be true after setting to true.");

        dto.setOnline(false);
        assertFalse(dto.isOnline(), "Online status should be false after setting to false.");
    }

    @Test
    public void testDifferentInstances() {
        UserProfileDTO dto1 = new UserProfileDTO();
        dto1.setId(10L);
        dto1.setUsername("profile1");
        dto1.setAvatarUrl(1);
        dto1.setOnline(true);

        UserProfileDTO dto2 = new UserProfileDTO();
        dto2.setId(20L);
        dto2.setUsername("profile2");
        dto2.setAvatarUrl(2);
        dto2.setOnline(false);

        // Assert values for dto1
        assertEquals(10L, dto1.getId());
        assertEquals("profile1", dto1.getUsername());
        assertEquals(1, dto1.getAvatarUrl());
        assertTrue(dto1.isOnline());

        // Assert values for dto2
        assertEquals(20L, dto2.getId());
        assertEquals("profile2", dto2.getUsername());
        assertEquals(2, dto2.getAvatarUrl());
        assertFalse(dto2.isOnline());

        // Ensure they are different
        assertNotEquals(dto1.getId(), dto2.getId());
        assertNotEquals(dto1.getUsername(), dto2.getUsername());
        assertNotEquals(dto1.getAvatarUrl(), dto2.getAvatarUrl());
        assertNotEquals(dto1.isOnline(), dto2.isOnline());
    }
}