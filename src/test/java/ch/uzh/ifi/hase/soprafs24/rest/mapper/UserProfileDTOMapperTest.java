package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileDTOMapperTest {

    @Test
    void testConvertEntityToUserProfileDTO() {
        // Create test user
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setProfileImage(2);
        user.setexperienceLevel(UserLevel.Intermediate);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setCreationDate(LocalDate.of(2024, 1, 1));
        user.setStatus(UserStatus.ONLINE);

        // Convert to DTO
        UserProfileDTO userProfileDTO = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(user);

        // Assert all fields are correctly mapped
        assertEquals(user.getId(), userProfileDTO.getId());
        assertEquals(user.getUsername(), userProfileDTO.getUsername());
        assertEquals(user.getName(), userProfileDTO.getDisplayName());
        assertEquals(user.getProfileImage(), userProfileDTO.getAvatarUrl());
        assertEquals(user.getexperienceLevel().toString(), userProfileDTO.getExperienceLevel().toString());
        assertEquals(user.getBirthday(), userProfileDTO.getBirthday());
        assertEquals(user.getCreationDate().atStartOfDay(), userProfileDTO.getCreatedAt());
        assertTrue(userProfileDTO.isOnline());
    }

    @Test
    void testConvertEntityToUserProfileDTO_withNullValues() {
        // Create test user with null values
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setStatus(UserStatus.OFFLINE);

        // Convert to DTO
        UserProfileDTO userProfileDTO = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(user);

        // Assert all fields are correctly mapped
        assertEquals(user.getId(), userProfileDTO.getId());
        assertEquals(user.getUsername(), userProfileDTO.getUsername());
        assertEquals(user.getName(), userProfileDTO.getDisplayName());
        assertNull(userProfileDTO.getExperienceLevel());
        assertNull(userProfileDTO.getBirthday());
        assertNull(userProfileDTO.getCreatedAt());
        assertFalse(userProfileDTO.isOnline());
    }

    @Test
    void testConvertEntityToUserProfileDTO_withDifferentStatus() {
        // Create test user with different status
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setStatus(UserStatus.OFFLINE);

        // Convert to DTO
        UserProfileDTO userProfileDTO = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(user);

        // Assert online status is correctly mapped
        assertFalse(userProfileDTO.isOnline());
    }
} 