package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOMapperTest {

    @Test
    void testConvertEntityToLoginResponseDTO() {
        // Create test user
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setProfileImage("test-image-url".getBytes());
        user.setLevel(UserLevel.Intermediate);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setCreationDate(LocalDate.of(2024, 1, 1));
        user.setStatus(UserStatus.ONLINE);
        user.setToken("test-token");

        // Convert to DTO
        LoginResponseDTO loginResponseDTO = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(user);

        // Assert token is correctly mapped
        assertEquals(user.getToken(), loginResponseDTO.getToken());
        
        // Assert UserProfile is correctly mapped
        UserProfileDTO userProfile = loginResponseDTO.getUser();
        assertNotNull(userProfile);
        assertEquals(user.getId(), userProfile.getId());
        assertEquals(user.getUsername(), userProfile.getUsername());
        assertEquals(user.getName(), userProfile.getDisplayName());
        assertEquals(new String(user.getProfileImage()), userProfile.getAvatarUrl());
        assertEquals(user.getLevel().toString(), userProfile.getExperienceLevel().toString());
        assertEquals(user.getBirthday(), userProfile.getBirthday());
        assertEquals(user.getCreationDate().atStartOfDay(), userProfile.getCreatedAt());
        assertTrue(userProfile.isOnline());
    }

    @Test
    void testConvertEntityToLoginResponseDTO_withNullValues() {
        // Create test user with minimal values
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken(null);

        // Convert to DTO
        LoginResponseDTO loginResponseDTO = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(user);

        // Assert token is null
        assertNull(loginResponseDTO.getToken());

        // Assert UserProfile is correctly mapped with null values
        UserProfileDTO userProfile = loginResponseDTO.getUser();
        assertNotNull(userProfile);
        assertEquals(user.getId(), userProfile.getId());
        assertEquals(user.getUsername(), userProfile.getUsername());
        assertEquals(user.getName(), userProfile.getDisplayName());
        assertNull(userProfile.getAvatarUrl());
        assertNull(userProfile.getExperienceLevel());
        assertNull(userProfile.getBirthday());
        assertNull(userProfile.getCreatedAt());
        assertFalse(userProfile.isOnline());
    }
} 