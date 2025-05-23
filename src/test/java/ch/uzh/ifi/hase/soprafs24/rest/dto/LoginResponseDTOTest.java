package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO.ExperienceLevel; // Assuming ExperienceLevel is part of UserProfileDTO

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginResponseDTOTest {

    private UserProfileDTO createSampleUserProfileDTO() {
        UserProfileDTO userProfile = new UserProfileDTO();
        userProfile.setId(1L);
        userProfile.setUsername("testuser");
        userProfile.setDisplayName("Test User Display");
        userProfile.setAvatarUrl(1);
        userProfile.setExperienceLevel(ExperienceLevel.Beginner);
        userProfile.setBirthday(LocalDate.of(1990, 1, 1));
        userProfile.setCreatedAt(LocalDateTime.now().minusDays(1));
        userProfile.setOnline(true);
        return userProfile;
    }

    @Test
    public void testGettersAndSetters() {
        LoginResponseDTO dto = new LoginResponseDTO();
        String token = "sample-jwt-token";
        UserProfileDTO userProfile = createSampleUserProfileDTO();

        // Set values
        dto.setToken(token);
        dto.setUser(userProfile);

        // Assert values using getters
        assertEquals(token, dto.getToken(), "Token should match the set value.");
        assertEquals(userProfile, dto.getUser(), "User profile DTO should match the set value.");

        // Assert some properties of the nested UserProfileDTO
        if (dto.getUser() != null) {
            assertEquals(1L, dto.getUser().getId(), "User ID in nested DTO should match.");
            assertEquals("testuser", dto.getUser().getUsername(), "Username in nested DTO should match.");
            assertTrue(dto.getUser().isOnline(), "Online status in nested DTO should match.");
        }
    }

    @Test
    public void testDefaultValues() {
        LoginResponseDTO dto = new LoginResponseDTO();

        // Assert default values (should be null for object types)
        assertNull(dto.getToken(), "Default token should be null.");
        assertNull(dto.getUser(), "Default user profile DTO should be null.");
    }

    @Test
    public void testSetNullValues() {
        LoginResponseDTO dto = new LoginResponseDTO();

        // Set fields to null
        dto.setToken(null);
        dto.setUser(null);

        // Assert that getters return null
        assertNull(dto.getToken(), "Token should be null after setting to null.");
        assertNull(dto.getUser(), "User profile DTO should be null after setting to null.");
    }

    @Test
    public void testDifferentInstances() {
        LoginResponseDTO dto1 = new LoginResponseDTO();
        UserProfileDTO user1 = createSampleUserProfileDTO();
        user1.setId(10L);
        user1.setUsername("user1");
        dto1.setToken("token1");
        dto1.setUser(user1);

        LoginResponseDTO dto2 = new LoginResponseDTO();
        UserProfileDTO user2 = createSampleUserProfileDTO(); // Create a new instance for user2
        user2.setId(20L);
        user2.setUsername("user2");
        dto2.setToken("token2");
        dto2.setUser(user2);

        // Assert values for dto1
        assertEquals("token1", dto1.getToken());
        assertEquals(10L, dto1.getUser().getId());
        assertEquals("user1", dto1.getUser().getUsername());

        // Assert values for dto2
        assertEquals("token2", dto2.getToken());
        assertEquals(20L, dto2.getUser().getId());
        assertEquals("user2", dto2.getUser().getUsername());

        // Ensure they are different
        assertNotEquals(dto1.getToken(), dto2.getToken());
        assertNotEquals(dto1.getUser().getId(), dto2.getUser().getId());
        assertNotEquals(dto1.getUser().getUsername(), dto2.getUser().getUsername());
    }

    @Test
    public void testSetUserToNewUserProfileDTO() {
        LoginResponseDTO dto = new LoginResponseDTO();
        UserProfileDTO initialUser = createSampleUserProfileDTO();
        initialUser.setId(5L);
        dto.setUser(initialUser);

        assertEquals(5L, dto.getUser().getId(), "Initial user ID should be set.");

        UserProfileDTO newUser = new UserProfileDTO(); // A completely new UserProfileDTO
        newUser.setId(6L);
        newUser.setUsername("newUser");
        newUser.setDisplayName("New User Display");
        newUser.setAvatarUrl(2);
        newUser.setExperienceLevel(ExperienceLevel.Intermediate);
        newUser.setBirthday(LocalDate.of(2000, 2, 2));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setOnline(false);

        dto.setUser(newUser);

        assertEquals(newUser, dto.getUser(), "User profile DTO should be updated to the new instance.");
        assertEquals(6L, dto.getUser().getId(), "Updated user ID should match.");
        assertEquals("newUser", dto.getUser().getUsername(), "Updated username should match.");
        assertEquals(ExperienceLevel.Intermediate, dto.getUser().getExperienceLevel(), "Updated experience level should match.");
    }
}