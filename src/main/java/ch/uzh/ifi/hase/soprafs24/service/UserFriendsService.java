package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.Optional;

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
    public void addFriendRequest(Long userId, User requestUser) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        // Check for duplicates (comparing by user ID)
        boolean exists = uf.getFriendRequests().stream()
            .anyMatch(u -> u.getId().equals(requestUser.getId()));
        if (!exists) {
            uf.getFriendRequests().add(requestUser);
            userFriendsRepository.save(uf);
        }
    }

    // Remove (or reject) a friend request by user ID of the request sender
    public void removeFriendRequest(Long userId, Long requestUserId) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        uf.getFriendRequests().removeIf(u -> u.getId().equals(requestUserId));
        userFriendsRepository.save(uf);
    }

    // Accept a friend request using a provided User object:
    // Remove the request and add the user to the friends list.
    public void acceptFriendRequest(Long userId, User requestUser) {
        UserFriends uf = userFriendsRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserFriends not found for userId: " + userId));
        // Remove the request; comparison by ID for reliability
        boolean removed = uf.getFriendRequests().removeIf(u -> u.getId().equals(requestUser.getId()));
        if (removed) {
            // Add to friends if not already present
            boolean alreadyFriend = uf.getFriends().stream()
                .anyMatch(u -> u.getId().equals(requestUser.getId()));
            if (!alreadyFriend) {
                uf.getFriends().add(requestUser);
            }
            userFriendsRepository.save(uf);
        }
    }
}
