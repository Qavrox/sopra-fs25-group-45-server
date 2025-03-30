package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserFriends;
import ch.uzh.ifi.hase.soprafs24.repository.UserFriendsRepository;

@ExtendWith(MockitoExtension.class)
public class UserFriendsServiceTest {

    @Mock
    private UserFriendsRepository userFriendsRepository;
    
    @InjectMocks
    private UserFriendsService userFriendsService;
    
    private User user;
    private User friend;
    private UserFriends userFriends;
    private UserFriends friendUserFriends;
    
    @BeforeEach
    public void setUp() {
        // Create a user and a friend with IDs
        user = new User();
        user.setId(1L);
        
        friend = new User();
        friend.setId(2L);
        
        // Setup UserFriends for the user
        userFriends = new UserFriends();
        userFriends.setFriends(new ArrayList<>());
        userFriends.setFriendRequests(new ArrayList<>());
        
        // Setup UserFriends for the friend
        friendUserFriends = new UserFriends();
        friendUserFriends.setFriends(new ArrayList<>());
        friendUserFriends.setFriendRequests(new ArrayList<>());
    }
    
    @Test
    public void testGetFriends_success() {
        // Prepare a friends list for the user
        List<User> friendsList = new ArrayList<>();
        friendsList.add(friend);
        userFriends.setFriends(friendsList);
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        
        List<User> result = userFriendsService.getFriends(user.getId());
        assertEquals(1, result.size());
        assertEquals(friend.getId(), result.get(0).getId());
    }
    
    @Test
    public void testGetFriends_notFound() {
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userFriendsService.getFriends(user.getId());
        });
        assertTrue(exception.getMessage().contains("UserFriends not found for userId: " + user.getId()));
    }
    
    @Test
    public void testGetFriendRequests_success() {
        // Prepare a friend request list for the user
        List<User> friendRequests = new ArrayList<>();
        friendRequests.add(friend);
        userFriends.setFriendRequests(friendRequests);
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        
        List<User> result = userFriendsService.getFriendRequests(user.getId());
        assertEquals(1, result.size());
        assertEquals(friend.getId(), result.get(0).getId());
    }
    
    @Test
    public void testAddFriendRequest_success() {
        // Friend's UserFriends initially has no friend requests
        when(userFriendsRepository.findById(friend.getId())).thenReturn(Optional.of(friendUserFriends));
        
        // Add friend request from user to friend
        userFriendsService.addFriendRequest(user, friend);
        
        // Verify that friendUserFriends now contains the user in friendRequests
        assertEquals(1, friendUserFriends.getFriendRequests().size());
        assertEquals(user.getId(), friendUserFriends.getFriendRequests().get(0).getId());
        verify(userFriendsRepository, times(1)).save(friendUserFriends);
    }
    
    @Test
    public void testAddFriendRequest_duplicate() {
        // Add the user as an existing friend request already
        List<User> requests = new ArrayList<>();
        requests.add(user);
        friendUserFriends.setFriendRequests(requests);
        when(userFriendsRepository.findById(friend.getId())).thenReturn(Optional.of(friendUserFriends));
        
        // Try adding duplicate friend request; it should not be added again
        userFriendsService.addFriendRequest(user, friend);
        
        // Verify that the request list still contains only one entry and no save occurs
        assertEquals(1, friendUserFriends.getFriendRequests().size());
        verify(userFriendsRepository, never()).save(friendUserFriends);
    }
    
    @Test
    public void testRemoveFriendRequest_success() {
        // Setup: the user's friendRequests list contains the friend
        List<User> requests = new ArrayList<>();
        requests.add(friend);
        userFriends.setFriendRequests(requests);
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        
        // Remove the friend request
        userFriendsService.removeFriendRequest(user.getId(), friend.getId());
        
        assertTrue(userFriends.getFriendRequests().isEmpty());
        verify(userFriendsRepository, times(1)).save(userFriends);
    }
    
    @Test
    public void testRemoveFriendRequest_notFound() {
        // Setup: no friend requests exist for the user
        userFriends.setFriendRequests(new ArrayList<>());
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        
        // Attempting to remove a non-existent friend request should complete silently
        userFriendsService.removeFriendRequest(user.getId(), friend.getId());
        assertTrue(userFriends.getFriendRequests().isEmpty());
        verify(userFriendsRepository, times(1)).save(userFriends);
    }
    
    @Test
    public void testAcceptFriendRequest_success() {
        // Setup: user has a pending friend request from friend
        List<User> requests = new ArrayList<>();
        requests.add(friend);
        userFriends.setFriendRequests(requests);
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        when(userFriendsRepository.findById(friend.getId())).thenReturn(Optional.of(friendUserFriends));
        
        // Accept the friend request
        userFriendsService.acceptFriendRequest(user, friend);
        
        // Verify that the friend request is removed and both users have each other in their friends list
        assertTrue(userFriends.getFriendRequests().isEmpty());
        assertEquals(1, userFriends.getFriends().size());
        assertEquals(friend.getId(), userFriends.getFriends().get(0).getId());
        assertEquals(1, friendUserFriends.getFriends().size());
        assertEquals(user.getId(), friendUserFriends.getFriends().get(0).getId());
        
        verify(userFriendsRepository, times(1)).save(userFriends);
        verify(userFriendsRepository, times(1)).save(friendUserFriends);
    }
    
    @Test
    public void testAcceptFriendRequest_notFoundInFriendRequests() {
        // Setup: user's friendRequests list does not contain friend
        userFriends.setFriendRequests(new ArrayList<>());
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        when(userFriendsRepository.findById(friend.getId())).thenReturn(Optional.of(friendUserFriends));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userFriendsService.acceptFriendRequest(user, friend);
        });
        assertTrue(exception.getMessage().contains("Friend request from userId: " + friend.getId() + " not found"));
    }
    
    @Test
    public void testAcceptFriendRequest_userNotFound() {
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userFriendsService.acceptFriendRequest(user, friend);
        });
        assertTrue(exception.getMessage().contains("UserFriends not found for userId: " + user.getId()));
    }
    
    @Test
    public void testAcceptFriendRequest_friendNotFound() {
        // Setup: user has a friend request from friend
        List<User> requests = new ArrayList<>();
        requests.add(friend);
        userFriends.setFriendRequests(requests);
        
        when(userFriendsRepository.findById(user.getId())).thenReturn(Optional.of(userFriends));
        when(userFriendsRepository.findById(friend.getId())).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userFriendsService.acceptFriendRequest(user, friend);
        });
        assertTrue(exception.getMessage().contains("UserFriends not found for friendId: " + friend.getId()));
    }
}