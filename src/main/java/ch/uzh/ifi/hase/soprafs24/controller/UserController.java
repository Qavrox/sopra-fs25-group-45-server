package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers(@RequestHeader("Authorization") String token) {
    // check if user is authorized
    userService.getUserByToken(token);
    
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/auth/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LoginResponseDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // login user
    User loggedInUser = userService.loginUser(userInput);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(loggedInUser);
  }

  @PostMapping("/auth/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logoutUser(@RequestHeader("Authorization") String token) {
    userService.logoutUser(token);
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable Long userId, @RequestHeader(value = "Authorization", required = false) String token) {
    // check if user is authorized
    userService.getUserByToken(token);
    
    // fetch user by ID
    User user = userService.getUserById(userId);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO updateUser(@PathVariable Long userId, @RequestBody UserPutDTO userPutDTO, @RequestHeader("Authorization") String token) {
    // check if user is authorized to update user
    User currentUser = userService.getUserByToken(token);
    if (!currentUser.getId().equals(userId)) {
      throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
    }
    
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

    // update user
    User updatedUser = userService.updateUser(userInput, token);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
  }

  @GetMapping("/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getFriends(@RequestHeader("Authorization") String token) {
    // fetch friends
    List<User> friends = userService.getFriends(token);
    List<UserGetDTO> friendDTOs = new ArrayList<>();

    // handle empty friends
    if (friends == null || friends.isEmpty()) {
      return friendDTOs;
    }

    // convert each friend to the API representation
    for (User friend : friends) {
      UserGetDTO friendDTO = new UserGetDTO();
      friendDTO.setId(friend.getId());
      friendDTO.setName(friend.getName());
      friendDTO.setUsername(friend.getUsername());
      friendDTOs.add(friendDTO);
    }
    return friendDTOs;
  }

  @PostMapping("/friends/{friendId}/request")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void sendFriendRequest(@PathVariable Long friendId, @RequestHeader("Authorization") String token) {
    userService.sendFriendRequest(friendId, token);
  }

  @PostMapping("/friends/{friendId}/accept")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void acceptFriendRequest(@PathVariable Long friendId, @RequestHeader("Authorization") String token) {
    userService.acceptFriendRequest(friendId, token);
  }

  @PostMapping("/friends/{friendId}/reject")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void rejectFriendRequest(@PathVariable Long friendId, @RequestHeader("Authorization") String token) {
    userService.rejectFriendRequest(friendId, token);
  }

  @GetMapping("/friends/requests")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getFriendRequests(@RequestHeader("Authorization") String token) {
    // fetch friend requests
    List<User> friendRequests = userService.getFriendRequests(token);
    List<UserGetDTO> friendRequestDTOs = new ArrayList<>();

    // handle empty friend requests
    if (friendRequests == null || friendRequests.isEmpty()) {
      return friendRequestDTOs;
    }

    // convert each friend request to the API representation
    for (User requester : friendRequests) {
      UserGetDTO requesterDTO = new UserGetDTO();
      requesterDTO.setId(requester.getId());
      requesterDTO.setName(requester.getName());
      requesterDTO.setUsername(requester.getUsername());
      friendRequestDTOs.add(requesterDTO);
    }
    return friendRequestDTOs;
  }
}
