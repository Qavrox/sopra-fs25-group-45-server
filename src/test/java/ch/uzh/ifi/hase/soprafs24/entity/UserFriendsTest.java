package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserFriendsTest {

    private UserFriends userFriends;
    private User user;
    private User friend1;
    private User friend2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        userFriends = new UserFriends();
        userFriends.setUser(user); // Sets the user and implicitly the userID via @MapsId

        friend1 = new User();
        friend1.setId(2L);
        friend1.setUsername("friendOne");

        friend2 = new User();
        friend2.setId(3L);
        friend2.setUsername("friendTwo");
    }

    @Test
    void testGetSetUser() {
        User newUser = new User();
        newUser.setId(10L);
        userFriends.setUser(newUser);
        assertEquals(newUser, userFriends.getUser());
        assertEquals(newUser.getId(), userFriends.getUserID());
    }

    @Test
    void testGetSetUserID() {
        // Note: Setting userID directly is usually managed by JPA through @MapsId
        // However, if there's a public setter, it should be tested.
        // Assuming UserFriends.setUserID exists and is relevant outside JPA context:
        userFriends.setUserID(5L); // This might be redundant if user is set correctly
        assertEquals(5L, userFriends.getUserID());
    }

    @Test
    void testGetSetFriendRequests() {
        List<User> requests = new ArrayList<>();
        requests.add(friend1);
        userFriends.setFriendRequests(requests);
        assertEquals(1, userFriends.getFriendRequests().size());
        assertTrue(userFriends.getFriendRequests().contains(friend1));
    }

    @Test
    void testAddFriendRequest() {
        userFriends.getFriendRequests().add(friend1);
        assertEquals(1, userFriends.getFriendRequests().size());
        assertTrue(userFriends.getFriendRequests().contains(friend1));
    }

    @Test
    void testRemoveFriendRequest() {
        userFriends.getFriendRequests().add(friend1);
        userFriends.getFriendRequests().remove(friend1);
        assertTrue(userFriends.getFriendRequests().isEmpty());
    }

    @Test
    void testGetSetFriends() {
        List<User> currentFriends = new ArrayList<>();
        currentFriends.add(friend2);
        userFriends.setFriends(currentFriends);
        assertEquals(1, userFriends.getFriends().size());
        assertTrue(userFriends.getFriends().contains(friend2));
    }

    @Test
    void testAddFriend() {
        userFriends.getFriends().add(friend1);
        userFriends.getFriends().add(friend2);
        assertEquals(2, userFriends.getFriends().size());
        assertTrue(userFriends.getFriends().contains(friend1));
        assertTrue(userFriends.getFriends().contains(friend2));
    }

    @Test
    void testRemoveFriend() {
        userFriends.getFriends().add(friend1);
        userFriends.getFriends().remove(friend1);
        assertTrue(userFriends.getFriends().isEmpty());
    }

    @Test
    void testInitialState() {
        UserFriends newUserFriends = new UserFriends();
        assertNull(newUserFriends.getUserID());
        assertNull(newUserFriends.getUser());
        assertNotNull(newUserFriends.getFriendRequests());
        assertTrue(newUserFriends.getFriendRequests().isEmpty());
        assertNotNull(newUserFriends.getFriends());
        assertTrue(newUserFriends.getFriends().isEmpty());
    }
}
