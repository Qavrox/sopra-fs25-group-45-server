package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    checkIfUserExists(newUser);
    newUser.setCreationDate(java.time.LocalDate.now());
    
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the username
   * defined in the User entity. The method will do nothing if the input is unique and throw
   * an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "name", "is"));
    }
}

  public User loginUser(User user) {
    User existingUser = userRepository.findByUsername(user.getUsername());
    
    if (existingUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    
    if (!existingUser.getPassword().equals(user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
    }
    
    existingUser.setStatus(UserStatus.ONLINE);
    existingUser.setToken(UUID.randomUUID().toString());
    
    userRepository.save(existingUser);
    userRepository.flush();
    
    return existingUser;
  }

  public User logoutUser(String token) {
    User user = getUserByToken(token);
    user.setStatus(UserStatus.OFFLINE);
    user.setToken(null);
    
    userRepository.save(user);
    userRepository.flush();
    
    return user;
  }

  public User getUserByToken(String token) {
      // Handle Bearer token
      if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7); 
      }

      if (token == null || token.isEmpty()) {
        throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "No token provided");
      }
      
      User user = userRepository.findByToken(token);
      if (user == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
      }
      return user;
  }

  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public User updateUser(User updatedUser, String token) {
    User existingUser = getUserByToken(token);
    
    if (updatedUser.getName() != null) {
      existingUser.setName(updatedUser.getName());
    }
    
    if (updatedUser.getexperienceLevel()!= null) {
      // Check if new level is valid
      if (!updatedUser.getexperienceLevel().name().equals("Beginner") &&
          !updatedUser.getexperienceLevel().name().equals("Intermediate") &&
          !updatedUser.getexperienceLevel().name().equals("Expert")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid level");
      }
      existingUser.setexperienceLevel(updatedUser.getexperienceLevel());
    }

    if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
      // Check if new username is unique
      User userByUsername = userRepository.findByUsername(updatedUser.getUsername());
      if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
      }
      existingUser.setUsername(updatedUser.getUsername());
    }
    
    if (updatedUser.getPassword() != null) {
      existingUser.setPassword(updatedUser.getPassword());
    }
    
    if (updatedUser.getBirthday() != null) {
      existingUser.setBirthday(updatedUser.getBirthday());
    }
    
    if (updatedUser.getProfileImage() != 0) {
      existingUser.setProfileImage(updatedUser.getProfileImage());
    }
    
    userRepository.save(existingUser);
    userRepository.flush();
    
    return existingUser;
  }

}
