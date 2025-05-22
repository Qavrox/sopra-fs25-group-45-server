package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setToken("token123");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDate.now());
        user.setProfileImage(1);
        user.setGamesPlayed(0L);
        user.setGamesWon(0L);
        user.setTotalWinnings(0L);
        user.setWinRate(0.0);
        user.setexperienceLevel(UserLevel.Beginner); // Corrected method name
    }

    @Test
    void testGetSetId() {
        user.setId(2L);
        assertEquals(2L, user.getId());
    }

    @Test
    void testGetSetName() {
        user.setName("New Name");
        assertEquals("New Name", user.getName());
    }

    @Test
    void testGetSetUsername() {
        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());
    }

    @Test
    void testGetSetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void testGetSetToken() {
        user.setToken("newToken");
        assertEquals("newToken", user.getToken());
    }

    @Test
    void testGetSetStatus() {
        user.setStatus(UserStatus.OFFLINE);
        assertEquals(UserStatus.OFFLINE, user.getStatus());
    }

    @Test
    void testGetSetCreationDate() {
        LocalDate newDate = LocalDate.now().minusDays(1);
        user.setCreationDate(newDate);
        assertEquals(newDate, user.getCreationDate());
    }

    @Test
    void testGetSetBirthday() {
        LocalDate birthday = LocalDate.of(1990, 1, 1);
        user.setBirthday(birthday);
        assertEquals(birthday, user.getBirthday());
    }
    
    @Test
    void testGetSetProfileImage() {
        user.setProfileImage(2);
        assertEquals(2, user.getProfileImage());
    }

    @Test
    void testGetSetGamesPlayed() {
        user.setGamesPlayed(10L);
        assertEquals(10L, user.getGamesPlayed());
    }

    @Test
    void testGetSetGamesWon() {
        user.setGamesWon(5L);
        assertEquals(5L, user.getGamesWon());
    }

    @Test
    void testGetSetWinRate() {
        user.setWinRate(50.0);
        assertEquals(50.0, user.getWinRate(), 0.001);
    }

    @Test
    void testGetSetTotalWinnings() {
        user.setTotalWinnings(1000L);
        assertEquals(1000L, user.getTotalWinnings());
    }

    @Test
    void testGetSetExperienceLevel() { // Corrected method name in test
        user.setexperienceLevel(UserLevel.Expert); // Corrected method name
        assertEquals(UserLevel.Expert, user.getexperienceLevel()); // Corrected method name
    }

    @Test
    void testGetSetFriends() {
        UserFriends friends = new UserFriends();
        friends.setUserID(user.getId()); // Assuming UserFriends has setUserID or similar
        user.setFriends(friends);
        assertNotNull(user.getFriends());
        assertEquals(user.getId(), user.getFriends().getUserID());
    }
    
    @Test
    void testUpdateWinRate_noGamesPlayed() {
        user.setGamesPlayed(0L);
        user.setGamesWon(0L);
        user.updateWinRate();
        assertEquals(0.0, user.getWinRate(), 0.001);
    }

    @Test
    void testUpdateWinRate_withGamesPlayed() {
        user.setGamesPlayed(10L);
        user.setGamesWon(5L);
        user.updateWinRate();
        assertEquals(50.0, user.getWinRate(), 0.001);
    }

    @Test
    void testUpdateWinRate_allGamesWon() {
        user.setGamesPlayed(5L);
        user.setGamesWon(5L);
        user.updateWinRate();
        assertEquals(100.0, user.getWinRate(), 0.001);
    }
}