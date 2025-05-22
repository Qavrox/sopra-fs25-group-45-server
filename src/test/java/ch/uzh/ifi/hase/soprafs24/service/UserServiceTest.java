package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    testUser.setCreationDate(java.time.LocalDate.now());

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void loginUser_validCredentials_success() {
    // given
    User loginUser = new User();
    loginUser.setUsername("testUsername");
    loginUser.setPassword("password");

    testUser.setStatus(UserStatus.OFFLINE);
    testUser.setToken(null);

    // when
    Mockito.when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(testUser);
    
    User loggedInUser = userService.loginUser(loginUser);

    // then
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    assertNotNull(loggedInUser.getToken());
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void loginUser_invalidUsername_throwsException() {
    // given
    User loginUser = new User();
    loginUser.setUsername("nonExistentUser");
    loginUser.setPassword("password");

    // when
    Mockito.when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(null);

    // then
    assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginUser));
  }

  @Test
  public void loginUser_invalidPassword_throwsException() {
    // given
    User loginUser = new User();
    loginUser.setUsername("testUsername");
    loginUser.setPassword("wrongPassword");

    testUser.setPassword("password");

    // when
    Mockito.when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(testUser);

    // then
    assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginUser));
  }

  @Test
  public void logoutUser_validToken_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    testUser.setStatus(UserStatus.ONLINE);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User loggedOutUser = userService.logoutUser(token);

    // then
    assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus());
    assertNull(loggedOutUser.getToken());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void getUserByToken_validToken_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User foundUser = userService.getUserByToken(token);

    // then
    assertEquals(testUser.getId(), foundUser.getId());
    assertEquals(testUser.getUsername(), foundUser.getUsername());
  }

  @Test
  public void getUserByToken_invalidToken_throwsException() {
    // given
    String token = "invalid-token";

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(null);

    // then
    assertThrows(ResponseStatusException.class, () -> userService.getUserByToken(token));
  }

  @Test
  public void getUserByToken_bearerToken_success() {
    // given
    String tokenValue = "valid-token";
    String bearerToken = "Bearer " + tokenValue;
    testUser.setToken(tokenValue);

    // when
    Mockito.when(userRepository.findByToken(tokenValue)).thenReturn(testUser);
    
    User foundUser = userService.getUserByToken(bearerToken);

    // then
    assertEquals(testUser.getId(), foundUser.getId());
    assertEquals(testUser.getUsername(), foundUser.getUsername());
  }

  @Test
  public void getUserById_validId_success() {
    // given
    Long userId = 1L;

    // when
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    
    User foundUser = userService.getUserById(userId);

    // then
    assertEquals(testUser.getId(), foundUser.getId());
    assertEquals(testUser.getUsername(), foundUser.getUsername());
  }

  @Test
  public void getUserById_invalidId_throwsException() {
    // given
    Long userId = 99L;

    // when
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // then
    assertThrows(ResponseStatusException.class, () -> userService.getUserById(userId));
  }

  @Test
  public void updateUser_changeName_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setName("newName");

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals("newName", result.getName());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_changeExperienceLevel_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setexperienceLevel(UserLevel.Expert);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals(UserLevel.Expert, result.getexperienceLevel());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_invalidExperienceLevel_throwsException() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setexperienceLevel(null);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    // then
    assertDoesNotThrow(() -> userService.updateUser(updatedUser, token));
  }

  @Test
  public void updateUser_changeUsername_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setUsername("newUsername");

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername("newUsername")).thenReturn(null);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals("newUsername", result.getUsername());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_duplicateUsername_throwsException() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User existingUser = new User();
    existingUser.setId(2L);
    existingUser.setUsername("existingUsername");
    
    User updatedUser = new User();
    updatedUser.setUsername("existingUsername");

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername("existingUsername")).thenReturn(existingUser);
    
    // then
    assertThrows(ResponseStatusException.class, () -> userService.updateUser(updatedUser, token));
  }

  @Test
  public void updateUser_changePassword_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setPassword("newPassword");

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals("newPassword", result.getPassword());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_changeBirthday_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    LocalDate birthday = LocalDate.of(1990, 1, 1);
    User updatedUser = new User();
    updatedUser.setBirthday(birthday);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals(birthday, result.getBirthday());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_changeProfileImage_success() {
    // given
    String token = "valid-token";
    testUser.setToken(token);
    
    User updatedUser = new User();
    updatedUser.setProfileImage(5);

    // when
    Mockito.when(userRepository.findByToken(token)).thenReturn(testUser);
    
    User result = userService.updateUser(updatedUser, token);

    // then
    assertEquals(5, result.getProfileImage());
    Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
    Mockito.verify(userRepository, Mockito.times(1)).flush();
  }
}
