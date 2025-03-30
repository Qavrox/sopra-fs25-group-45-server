package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserFriendsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
  private final UserFriendsService userFriendsService;

  UserController(UserService userService, UserFriendsService userFriendsService) {
    this.userService = userService;
    this.userFriendsService = userFriendsService;
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
  public LoginResponseDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    User loggedInUser = userService.loginUser(createdUser);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(loggedInUser);
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
    public List<UserFriendDTO> getFriends(@RequestHeader(value = "Authorization") String authHeader) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
      }

      List<UserFriendDTO> userFriends = new ArrayList<>();

      // To be implemented
      User user = userService.getUserByToken(authHeader);

      for (User friend : userFriendsService.getFriends(user.getId())) {
        userFriends.add(DTOMapper.INSTANCE.convertEntityToUserFriendDTO(friend));
      }

      return userFriends;
    }
    
    @PostMapping("/friends/{friendId}/request")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void requestFriend(@RequestHeader(value = "Authorization") String authHeader, @PathVariable long friendId) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
      }
      // TODO: Validate token here

      List<UserFriendDTO> userFriends = new ArrayList<>();

      // To be implemented
      User user = userService.getUserByToken(authHeader);
      User friend = userService.getUserById(friendId);

      userFriendsService.addFriendRequest(user.getId(), friend);
    }
    
    @PostMapping("/friends/{friendId}/accept")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void acceptFriend(@RequestHeader(value = "Authorization") String authHeader, @PathVariable long friendId) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
      }
      
      // TODO: Validate token here
        
      // To be implemented
      User user = userService.getUserByToken(authHeader);
      User friend = userService.getUserById(friend);

      userFriendsService.acceptFriendRequest(user.getId(), friend);
    }

    @PostMapping("/friends/{friendId}/reject")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void rejectFriend(@RequestHeader(value = "Authorization") String authHeader, @PathVariable long friendId) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
      }
      
      // TODO: Validate token here

      // To be implemented
      User user = userService.getUserByToken(authHeader);
      User friend = userService.getUserById(friend);
        
      userFriendsService.removeFriendRequest(user.getId(), friend.getId());
    }
}
