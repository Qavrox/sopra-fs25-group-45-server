package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserFriendsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 


/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserFriendsService userFriendsService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setCreationDate(java.time.LocalDate.now());
    user.setToken("valid-token");
  
    List<User> allUsers = Collections.singletonList(user);
  
    // this mocks the UserService -> we define above what the userService should return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);
    given(userService.getUserByToken(Mockito.anyString())).willReturn(user);
  
    // when
    MockHttpServletRequestBuilder getRequest = get("/users")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
  
    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].online", is((user.getStatus()==UserStatus.ONLINE))));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    user.setCreationDate(java.time.LocalDate.now());
  
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");
  
    given(userService.createUser(Mockito.any())).willReturn(user);
    given(userService.loginUser(Mockito.any())).willReturn(user);
  
    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));
  
    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.user.username", is(user.getUsername())))
        .andExpect(jsonPath("$.user.displayName", is(user.getName())))
        .andExpect(jsonPath("$.user.online", is(true)))
        .andExpect(jsonPath("$.token", is(user.getToken())));
  }

  @Test
  public void loginUser_validInput_userLoggedIn() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    user.setCreationDate(java.time.LocalDate.now());
  
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");
  
    given(userService.loginUser(Mockito.any())).willReturn(user);
  
    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));
  
    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.user.username", is(user.getUsername())))
        .andExpect(jsonPath("$.user.displayName", is(user.getName())))
        .andExpect(jsonPath("$.user.online", is(true)))
        .andExpect(jsonPath("$.token", is(user.getToken())));
  }

  @Test
  public void logoutUser_validToken_success() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken(null); // Token is null after logout
    user.setStatus(UserStatus.OFFLINE);
    
    String token = "Bearer valid-token";
    
    // Mock the service to return a logged out user
    given(userService.logoutUser(token)).willReturn(user);
    
    // when/then
    MockHttpServletRequestBuilder postRequest = post("/auth/logout")
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON);
    
    mockMvc.perform(postRequest)
        .andExpect(status().isOk());
  }

  @Test
  public void getUser_validUserId_returnsUser() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setStatus(UserStatus.ONLINE);
    user.setCreationDate(java.time.LocalDate.now());
    user.setToken("valid-token");
    
    given(userService.getUserById(1L)).willReturn(user);
    given(userService.getUserByToken(Mockito.anyString())).willReturn(user);
    
    // when
    MockHttpServletRequestBuilder getRequest = get("/users/1")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.online", is(true)));
  }

  @Test
  public void updateUser_validInput_userUpdated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Old Name");
    user.setUsername("oldUsername");
    user.setToken("valid-token");
    user.setStatus(UserStatus.ONLINE);
    
    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setName("New Name");
    updatedUser.setUsername("newUsername");
    updatedUser.setToken("valid-token");
    updatedUser.setStatus(UserStatus.ONLINE);
    
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setName("New Name");
    userPutDTO.setUsername("newUsername");
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userService.updateUser(Mockito.any(), Mockito.anyString())).willReturn(updatedUser);
    
    // when
    MockHttpServletRequestBuilder putRequest = put("/users/1")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPutDTO));
    
    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(updatedUser.getId().intValue())))
        .andExpect(jsonPath("$.name", is(updatedUser.getName())))
        .andExpect(jsonPath("$.username", is(updatedUser.getUsername())));
  }

  @Test
  public void getFriends_validToken_returnsFriendsList() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("valid-token");
    
    User friend = new User();
    friend.setId(2L);
    friend.setName("Friend User");
    friend.setUsername("friendUsername");
    
    List<User> friends = Collections.singletonList(friend);
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userFriendsService.getFriends(1L)).willReturn(friends);
    
    // when
    MockHttpServletRequestBuilder getRequest = get("/friends")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(friend.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(friend.getUsername())));
  }

  @Test
  public void getFriendRequests_validToken_returnsRequestsList() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("valid-token");
    
    User requester = new User();
    requester.setId(2L);
    requester.setName("Requester User");
    requester.setUsername("requesterUsername");
    
    List<User> requests = Collections.singletonList(requester);
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userFriendsService.getFriendRequests(1L)).willReturn(requests);
    
    // when
    MockHttpServletRequestBuilder getRequest = get("/friends/requests")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(requester.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(requester.getUsername())));
  }

  @Test
  public void requestFriend_validData_success() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("valid-token");
    
    User friend = new User();
    friend.setId(2L);
    friend.setName("Friend User");
    friend.setUsername("friendUsername");
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userService.getUserById(2L)).willReturn(friend);
    doNothing().when(userFriendsService).addFriendRequest(user, friend);
    
    // when
    MockHttpServletRequestBuilder postRequest = post("/friends/2/request")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated());
  }

  @Test
  public void acceptFriend_validData_success() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("valid-token");
    
    User friend = new User();
    friend.setId(2L);
    friend.setName("Friend User");
    friend.setUsername("friendUsername");
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userService.getUserById(2L)).willReturn(friend);
    doNothing().when(userFriendsService).acceptFriendRequest(user, friend);
    
    // when
    MockHttpServletRequestBuilder postRequest = post("/friends/2/accept")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk());
    
    verify(userFriendsService).acceptFriendRequest(user, friend);
  }

  @Test
  public void rejectFriend_validData_success() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("valid-token");
    
    User friend = new User();
    friend.setId(2L);
    friend.setName("Friend User");
    friend.setUsername("friendUsername");
    
    given(userService.getUserByToken("Bearer valid-token")).willReturn(user);
    given(userService.getUserById(2L)).willReturn(friend);
    doNothing().when(userFriendsService).removeFriendRequest(1L, 2L);
    
    // when
    MockHttpServletRequestBuilder postRequest = post("/friends/2/reject")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON);
    
    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk());
    
    verify(userFriendsService).removeFriendRequest(1L, 2L);
  }

  @Test
  public void updateUser_mismatchedUserId_throwsForbidden() throws Exception {
    // given
    User currentUser = new User(); // User making the request
    currentUser.setId(1L); // Current user's ID
    currentUser.setToken("valid-token");

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setName("New Name");
    userPutDTO.setUsername("newUsername");

    given(userService.getUserByToken("Bearer valid-token")).willReturn(currentUser);
    // No need to mock userService.updateUser as the controller should throw exception before calling it.

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/2") // Attempting to update user with ID 2
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPutDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isForbidden());
  }
  
  @Test
  public void updateUser_invalidToken_throwsUnauthorized() throws Exception {
    // given
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setName("New Name");
    userPutDTO.setUsername("newUsername");

    // Mock userService.getUserByToken to throw an exception for an invalid token
    given(userService.getUserByToken("Bearer invalid-token"))
        .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/1")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPutDTO));
    
    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isUnauthorized());
  }


  @Test
  public void getFriends_nullAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends")
            .contentType(MediaType.APPLICATION_JSON)) // No Authorization header
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getFriends_invalidAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends")
            .header("Authorization", "InvalidPrefix token") // Invalid prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }


  @Test
  public void friendRequestList_nullAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .contentType(MediaType.APPLICATION_JSON)) // No Authorization header
        .andExpect(status().isBadRequest());
  }

  @Test
  public void friendRequestList_invalidAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .header("Authorization", "TokenOnly") // Invalid format
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }


  @Test
  public void requestFriend_nullAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/request")
            .contentType(MediaType.APPLICATION_JSON)) // No Authorization header
        .andExpect(status().isBadRequest());
  }

  @Test
  public void requestFriend_invalidAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/request")
            .header("Authorization", "NoBearerPrefix 123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void acceptFriend_nullAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/accept")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void acceptFriend_invalidAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/accept")
            .header("Authorization", "BearerTokenWithoutSpace") // Invalid format
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }


  @Test
  public void rejectFriend_nullAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/reject")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void rejectFriend_invalidAuthHeader_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/reject")
            .header("Authorization", "  Bearer withLeadingSpace") // Invalid format
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getFriends_headerPresentButInvalidPrefix_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends")
            .header("Authorization", "NotBearer some_token_value") // Present, but wrong prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized()); // Expect 401 from controller logic
  }

  @Test
  public void friendRequestList_headerPresentButInvalidPrefix_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/friends/requests")
            .header("Authorization", "InvalidScheme other_data") // Present, but wrong prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized()); // Expect 401 from controller logic
  }

  @Test
  public void requestFriend_headerPresentButInvalidPrefix_throwsUnauthorized() throws Exception {
    // No specific mocks for userService or userFriendsService are needed if the auth check fails first.
    mockMvc.perform(post("/friends/2/request") // Assuming a valid friendId like 2
            .header("Authorization", "Malformed BearerToken") // Present, but wrong prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized()); // Expect 401 from controller logic
  }

  @Test
  public void acceptFriend_headerPresentButInvalidPrefix_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/accept") // Assuming a valid friendId like 2
            .header("Authorization", "TokenIsHereButNoBearer") // Present, but wrong prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized()); // Expect 401 from controller logic
  }

  @Test
  public void rejectFriend_headerPresentButInvalidPrefix_throwsUnauthorized() throws Exception {
    mockMvc.perform(post("/friends/2/reject") // Assuming a valid friendId like 2
            .header("Authorization", "MY_CUSTOM_TOKEN_FORMAT") // Present, but wrong prefix
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized()); // Expect 401 from controller logic
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}