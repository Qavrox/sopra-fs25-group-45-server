package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserFriends;
import ch.uzh.ifi.hase.soprafs24.repository.UserFriendsRepository;

@Service
@Transactional
public class UserFriendsService {
    private final Logger log = LoggerFactory.getLogger(UserFriendsService.class);

    private final UserFriendsRepository userFriendsRepository;

    @Autowired
    public UserFriendsService(@Qualifier("userFriendsRepository") UserFriendsRepository userFriendsRepository) {
        this.userFriendsRepository = userFriendsRepository;
    }

    // Retrieve the friends list for a given user
    public List<User> getFriends(Long userId) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        return uf.getFriends();
    }

    // Retrieve the friend requests for a given user
    public List<User> getFriendRequests(Long userId) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        return uf.getFriendRequests();
    }

    // Add a friend request using a provided User object
    public void addFriendRequest(User user, User friend) {
        UserFriends uf = userFriendsRepository.findById(friend.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend not found for userId: " + friend.getId()));
        
        // Check for duplicates (comparing by user ID)
        boolean exists = uf.getFriendRequests().stream()
            .anyMatch(u -> u.getId().equals(user.getId()));
        
        if (!exists) {
            List<User> friendRequests = uf.getFriendRequests();
            friendRequests.add(user);
            uf.setFriendRequests(friendRequests);
            userFriendsRepository.save(uf);
        }
    }

    // Remove (or reject) a friend request by user ID of the request sender
    public void removeFriendRequest(Long userId, Long friendId) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        
        List<User> friendRequests = uf.getFriendRequests();
        friendRequests.removeIf(u -> u.getId().equals(friendId));
        uf.setFriendRequests(friendRequests);
        userFriendsRepository.save(uf);
    }

    // Accept a friend request using a provided User object:
    // Remove the request and add each user to the other user's friend list.
    public void acceptFriendRequest(User user, User friend) {
        // Retrieve the UserFriends entity for the user
        UserFriends userUF = userFriendsRepository.findById(user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + user.getId()));
        
        // Retrieve the UserFriends entity for the friend
        UserFriends friendUF = userFriendsRepository.findById(friend.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for friendId: " + friend.getId()));
        
        // Remove the friend request from the user's friendRequests list
        List<User> friendRequests = userUF.getFriendRequests();
        boolean removed = friendRequests.removeIf(u -> u.getId().equals(friend.getId()));
        
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request from userId: " + friend.getId() + " not found");
        } else {
            userUF.setFriendRequests(friendRequests);
        }

        // Add friend to user's friends list if not already present
        if (userUF.getFriends().stream().noneMatch(u -> u.getId().equals(friend.getId()))) {
            List<User> friends = userUF.getFriends();
            friends.add(friend);
            userUF.setFriends(friends);
        }

        // Add user to friend's friends list if not already present
        if (friendUF.getFriends().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            List<User> friends = friendUF.getFriends();
            friends.add(user);
            friendUF.setFriends(friends);
        }

        // Save both entities
        userFriendsRepository.save(userUF);
        userFriendsRepository.save(friendUF);
    }
}
