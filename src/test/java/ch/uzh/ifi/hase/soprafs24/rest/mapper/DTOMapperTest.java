package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreationPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameHistoryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LoginResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserFriendDTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId; // Added for date conversion
import java.util.Date;   // Added for date conversion
import java.util.List;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("name");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getName(), user.getName());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getName(), userGetDTO.getName());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus()==UserStatus.ONLINE, userGetDTO.getOnline());
  }

  @Test
  public void testGetGameFromUserPostDTOToGameEntity() {

        
    GameCreationPostDTO gamePostDTO = new GameCreationPostDTO();
    gamePostDTO.setPublic(true);
    gamePostDTO.setMaximalPlayers(5);
    gamePostDTO.setStartCredit(1000);
    gamePostDTO.setSmallBlind(10);
    gamePostDTO.setBigBlind(20);


    Game game = DTOMapper.INSTANCE.convertCreateGameDTOToGameEntity(gamePostDTO);

    assertEquals(gamePostDTO.getIsPublic(), game.getIsPublic());
    assertEquals(gamePostDTO.getMaximalPlayers(), game.getMaximalPlayers());
    assertEquals(gamePostDTO.getStartCredit(), game.getStartCredit());
    assertEquals(gamePostDTO.getSmallBlind(), game.getSmallBlind());
    assertEquals(gamePostDTO.getBigBlind(), game.getBigBlind());
  }

  @Test
  public void testEntityToGameGetDTO() {
    Game game = new Game();
    game.setId(1L);
    game.setIsPublic(true);
    game.setMaximalPlayers(5);
    game.setStartCredit(1000L);
    game.setSmallBlind(10);
    game.setBigBlind(20);
    game.setSmallBlindIndex(0);
    game.setPot(1100L);
    game.setCallAmount(100L);
    game.setGameStatus(GameStatus.WAITING);

    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    assertEquals(game.getId(), gameGetDTO.getId());
    assertEquals(game.getIsPublic(), gameGetDTO.getIsPublic());
    assertEquals(game.getMaximalPlayers(), gameGetDTO.getMaximalPlayers());
    assertEquals(game.getStartCredit(), gameGetDTO.getStartCredit());
    assertEquals(game.getSmallBlind(), gameGetDTO.getSmallBlind());
    assertEquals(game.getBigBlind(), gameGetDTO.getBigBlind());
    assertEquals(game.getSmallBlindIndex(), gameGetDTO.getSmallBlindIndex());
    assertEquals(game.getPot(), gameGetDTO.getPot());
    assertEquals(game.getCallAmount(), gameGetDTO.getCallAmount());
    assertEquals(game.getGameStatus(), gameGetDTO.getGameStatus());
  }

  @Test
  public void testgetLoginResponseDTOfromEntity() {
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setProfileImage(1);
    user.setBirthday(LocalDate.of(1995, 5, 5));
    user.setCreationDate(LocalDate.now());
    user.setexperienceLevel(UserLevel.Beginner);

    LoginResponseDTO loginResponseDTO = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(user);

    assertEquals(user.getToken(), loginResponseDTO.getToken());
    assertNotNull(loginResponseDTO.getUser());
    assertEquals(user.getId(), (loginResponseDTO.getUser()).getId());
    assertEquals(user.getName(), (loginResponseDTO.getUser()).getDisplayName());
    assertEquals(user.getUsername(), loginResponseDTO.getUser().getUsername());
    assertEquals(user.getProfileImage(), (loginResponseDTO.getUser()).getAvatarUrl());
    assertEquals(user.getCreationDate().atStartOfDay(), (loginResponseDTO.getUser()).getCreatedAt());
    assertEquals(user.getBirthday(), (loginResponseDTO.getUser()).getBirthday());
    assertEquals(user.getexperienceLevel().toString(), (loginResponseDTO.getUser()).getExperienceLevel().toString());
    assertEquals(user.getStatus() == UserStatus.ONLINE, (loginResponseDTO.getUser()).isOnline());

    User userWithNulls = new User();
    userWithNulls.setId(2L);
    userWithNulls.setName("Another User");
    userWithNulls.setUsername("anotheruser");
    userWithNulls.setStatus(UserStatus.OFFLINE);

    LoginResponseDTO loginResponseDTONulls = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(userWithNulls);

    assertNull(loginResponseDTONulls.getToken());
    assertNotNull(loginResponseDTONulls.getUser());
    assertEquals(userWithNulls.getId(), (loginResponseDTONulls.getUser()).getId());
    assertEquals(userWithNulls.getName(), (loginResponseDTONulls.getUser()).getDisplayName());
    assertEquals(userWithNulls.getUsername(), (loginResponseDTONulls.getUser()).getUsername());
    assertEquals(0, (loginResponseDTONulls.getUser()).getAvatarUrl()); 
    assertNull((loginResponseDTONulls.getUser()).getCreatedAt());
    assertNull((loginResponseDTONulls.getUser()).getBirthday());
    assertNull((loginResponseDTONulls.getUser()).getExperienceLevel()); 
    assertFalse((loginResponseDTONulls.getUser()).isOnline());
  }

  @Test
  public void testUserFriendDTOfromEntity() {
    User user = new User();
    user.setName("Firstname Lastname"); 
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE); 
    user.setToken("1"); 
    user.setCreationDate(LocalDate.now()); // Set a creation date

    UserFriendDTO userFriendDTO = DTOMapper.INSTANCE.convertEntityToUserFriendDTO(user);

    assertEquals(user.getId(), userFriendDTO.getId());
    assertEquals(user.getUsername(), userFriendDTO.getUsername());
    assertEquals(user.getStatus(), userFriendDTO.getOnline());

    // Corrected assertion for creationDate (line 206 in previous structure)
    // Assuming UserFriendDTO.createdAt is java.util.Date based on error format
    assertNotNull(userFriendDTO.getCreatedAt(), "CreatedAt in DTO should not be null");
    if (userFriendDTO.getCreatedAt() instanceof java.util.Date) {
        java.util.Date utilDate = (java.util.Date) userFriendDTO.getCreatedAt();
        LocalDate actualCreatedAtDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        assertEquals(user.getCreationDate(), actualCreatedAtDate);
    } else {
        // Fallback or fail if the type is not java.util.Date as error suggested
        // This might happen if UserFriendDTO.createdAt is LocalDate or LocalDateTime
        // If it's LocalDate:
        // assertEquals(user.getCreationDate(), userFriendDTO.getCreatedAt());
        // If it's LocalDateTime:
        // assertEquals(user.getCreationDate(), ((LocalDateTime)userFriendDTO.getCreatedAt()).toLocalDate());
        // For now, let's make it fail if not java.util.Date to highlight the assumption clearly
        assertTrue(userFriendDTO.getCreatedAt() instanceof java.util.Date, 
                   "UserFriendDTO.createdAt was expected to be java.util.Date but was: " + userFriendDTO.getCreatedAt().getClass().getName());
    }
    assertNull(userFriendDTO.getBirthday()); 
  }

  @Test
  public void testUserfromUserPutDTOtoEntity() {
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setName("Firstname Lastname");
    userPutDTO.setUsername("firstname@lastname");
    userPutDTO.setexperienceLevel(UserLevel.Beginner);
    userPutDTO.setPassword("password");
    userPutDTO.setBirthday(LocalDate.of(1990, 1, 1));
    userPutDTO.setProfileImage(1);


    User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

    assertEquals(userPutDTO.getName(), user.getName());
    assertEquals(userPutDTO.getUsername(), user.getUsername());
    assertEquals(userPutDTO.getexperienceLevel(), user.getexperienceLevel());
    assertEquals(userPutDTO.getPassword(), user.getPassword());
    assertEquals(userPutDTO.getBirthday(), user.getBirthday());
    assertEquals(userPutDTO.getProfileImage(), user.getProfileImage());
  }

  @Test
  public void testUserProfileDTOfromEntity() {
    // Scenario 1: User with UserLevel.Intermediate
    User userIntermediate = new User();
    userIntermediate.setId(1L);
    userIntermediate.setName("Firstname Lastname");
    userIntermediate.setUsername("firstname@lastname");
    userIntermediate.setStatus(UserStatus.ONLINE);
    userIntermediate.setProfileImage(2);
    userIntermediate.setexperienceLevel(UserLevel.Intermediate); // Test with Intermediate
    userIntermediate.setBirthday(LocalDate.of(1990, 1, 1));
    userIntermediate.setCreationDate(LocalDate.of(2024, 1, 1));

    UserProfileDTO userProfileDTOIntermediate = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(userIntermediate);

    assertEquals(userIntermediate.getId(), userProfileDTOIntermediate.getId());
    assertEquals(userIntermediate.getName(), userProfileDTOIntermediate.getDisplayName());
    assertEquals(userIntermediate.getUsername(), userProfileDTOIntermediate.getUsername());
    assertEquals(userIntermediate.getProfileImage(), userProfileDTOIntermediate.getAvatarUrl());
    assertNotNull(userProfileDTOIntermediate.getExperienceLevel()); // Should not be null here
    assertEquals(userIntermediate.getexperienceLevel().toString(), userProfileDTOIntermediate.getExperienceLevel().toString()); // Enum.toString() vs DTO.String.toString()
    assertEquals(userIntermediate.getBirthday(), userProfileDTOIntermediate.getBirthday());
    assertEquals(userIntermediate.getCreationDate().atStartOfDay(), userProfileDTOIntermediate.getCreatedAt());
    assertTrue(userProfileDTOIntermediate.isOnline());

    // Scenario 2: User with UserLevel.Expert (assuming UserLevel.Expert exists)
    User userExpert = new User();
    userExpert.setId(2L);
    userExpert.setName("Expert Player");
    userExpert.setUsername("expert@player");
    userExpert.setStatus(UserStatus.ONLINE);
    userExpert.setProfileImage(3);
    // Assuming UserLevel.Expert exists. If not, replace with another valid UserLevel or handle appropriately.
    try {
        userExpert.setexperienceLevel(UserLevel.valueOf("Expert"));
    } catch (IllegalArgumentException e) {
        System.err.println("Warning: UserLevel.Expert enum constant not found. Skipping setting 'Expert' level for test. Experience level will be null.");
        userExpert.setexperienceLevel(null); // Default to null if "Expert" isn't a valid enum constant
    }
    userExpert.setBirthday(LocalDate.of(1985, 5, 15));
    userExpert.setCreationDate(LocalDate.of(2023, 2, 20));

    UserProfileDTO userProfileDTOExpert = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(userExpert);

    assertEquals(userExpert.getId(), userProfileDTOExpert.getId());
    assertEquals(userExpert.getName(), userProfileDTOExpert.getDisplayName());
    assertEquals(userExpert.getUsername(), userProfileDTOExpert.getUsername());
    assertEquals(userExpert.getProfileImage(), userProfileDTOExpert.getAvatarUrl());
    if (userExpert.getexperienceLevel() != null) {
        assertNotNull(userProfileDTOExpert.getExperienceLevel());
        assertEquals(userExpert.getexperienceLevel().toString(), userProfileDTOExpert.getExperienceLevel().toString());
    } else {
        assertNull(userProfileDTOExpert.getExperienceLevel()); // If UserLevel.Expert wasn't found and set to null
    }
    assertEquals(userExpert.getBirthday(), userProfileDTOExpert.getBirthday());
    assertEquals(userExpert.getCreationDate().atStartOfDay(), userProfileDTOExpert.getCreatedAt());
    assertTrue(userProfileDTOExpert.isOnline());

    // Scenario 3: User with null experienceLevel and OFFLINE status
    User userNullExperience = new User();
    userNullExperience.setId(3L);
    userNullExperience.setName("Another One");
    userNullExperience.setUsername("anotherone");
    userNullExperience.setStatus(UserStatus.OFFLINE);
    userNullExperience.setProfileImage(0); // Explicitly 0, or let it default
    userNullExperience.setexperienceLevel(null); // Test with null experienceLevel
    userNullExperience.setBirthday(null); // Test with null birthday
    userNullExperience.setCreationDate(null); // Test with null creationDate

    UserProfileDTO userProfileDTONullExperience = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(userNullExperience);

    assertEquals(userNullExperience.getId(), userProfileDTONullExperience.getId());
    assertEquals(userNullExperience.getName(), userProfileDTONullExperience.getDisplayName());
    assertEquals(userNullExperience.getUsername(), userProfileDTONullExperience.getUsername());
    assertEquals(userNullExperience.getProfileImage(), userProfileDTONullExperience.getAvatarUrl()); // Will be 0
    assertNull(userProfileDTONullExperience.getExperienceLevel()); // Should be null
    assertNull(userProfileDTONullExperience.getBirthday());
    assertNull(userProfileDTONullExperience.getCreatedAt());
    assertFalse(userProfileDTONullExperience.isOnline());
  }

  @Test
  public void testGameHistoryDTOfromEntity() {
    GameHistory gameHistory = new GameHistory();
    gameHistory.setId(1L);
    gameHistory.setGameId(1L);
    gameHistory.setUserId(1L);
    gameHistory.setResult("Win");
    gameHistory.setWinnings(100L);
    gameHistory.setOtherPlayerIds(List.of(2L, 3L));
    gameHistory.setPlayedAt(LocalDateTime.now());

    GameHistoryDTO gameHistoryDTO = DTOMapper.INSTANCE.convertEntityToGameHistoryDTO(gameHistory);

    assertEquals(gameHistory.getId(), gameHistoryDTO.getId());
    assertEquals(gameHistory.getGameId(), gameHistoryDTO.getGameId());
    assertEquals(gameHistory.getUserId(), gameHistoryDTO.getUserId());
    assertEquals(gameHistory.getResult(), gameHistoryDTO.getResult());
    assertEquals(gameHistory.getWinnings(), gameHistoryDTO.getWinnings());
    assertEquals(gameHistory.getOtherPlayerIds(), gameHistoryDTO.getOtherPlayerIds());
    assertEquals(gameHistory.getPlayedAt(), gameHistoryDTO.getPlayedAt());
  }
  
  @Test
  public void testGameHistoryfromGameHistoryDTOtoEntity() {
    GameHistoryDTO gameHistoryDTO = new GameHistoryDTO();
    gameHistoryDTO.setId(1L);
    gameHistoryDTO.setGameId(1L);
    gameHistoryDTO.setUserId(1L);
    gameHistoryDTO.setResult("Win");
    gameHistoryDTO.setWinnings(100L);
    gameHistoryDTO.setOtherPlayerIds(List.of(2L, 3L));
    gameHistoryDTO.setPlayedAt(LocalDateTime.now());

    GameHistory gameHistory = DTOMapper.INSTANCE.convertGameHistoryDTOtoEntity(gameHistoryDTO);

    assertEquals(gameHistoryDTO.getId(), gameHistory.getId());
    assertEquals(gameHistoryDTO.getGameId(), gameHistory.getGameId());
    assertEquals(gameHistoryDTO.getUserId(), gameHistory.getUserId());
    assertEquals(gameHistoryDTO.getResult(), gameHistory.getResult());
    assertEquals(gameHistoryDTO.getWinnings(), gameHistory.getWinnings());
    assertEquals(gameHistoryDTO.getOtherPlayerIds(), gameHistory.getOtherPlayerIds());
    assertEquals(gameHistoryDTO.getPlayedAt(), gameHistory.getPlayedAt());
  }

  @Test
  public void testConvertEntityListToUserGetDTOList_success() {
    User user1 = new User();
    user1.setName("User One");
    user1.setUsername("userone");
    user1.setStatus(UserStatus.ONLINE);
    user1.setToken("token1");

    User user2 = new User();
    user2.setName("User Two");
    user2.setUsername("usertwo");
    user2.setStatus(UserStatus.OFFLINE);
    user2.setToken("token2");

    List<User> users = List.of(user1, user2);

    List<UserGetDTO> userGetDTOs = DTOMapper.INSTANCE.convertEntityListToUserGetDTOList(users);

    assertEquals(2, userGetDTOs.size());

    UserGetDTO userGetDTO1 = userGetDTOs.get(0);
    assertEquals(user1.getId(), userGetDTO1.getId());
    assertEquals(user1.getName(), userGetDTO1.getName());
    assertEquals(user1.getUsername(), userGetDTO1.getUsername());
    assertEquals(user1.getStatus() == UserStatus.ONLINE, userGetDTO1.getOnline());

    UserGetDTO userGetDTO2 = userGetDTOs.get(1);
    assertEquals(user2.getId(), userGetDTO2.getId());
    assertEquals(user2.getName(), userGetDTO2.getName());
    assertEquals(user2.getUsername(), userGetDTO2.getUsername());
    assertEquals(user2.getStatus() == UserStatus.ONLINE, userGetDTO2.getOnline());
  }

  @Test
  public void testByteArrayToString_NonNullInput() {
    String originalString = "Hello World!";
    byte[] byteArray = originalString.getBytes(StandardCharsets.UTF_8);

    String convertedString = DTOMapper.INSTANCE.byteArrayToString(byteArray);

    assertEquals(originalString, convertedString);
  }

  @Test
  public void testByteArrayToString_NullInput() {
    byte[] byteArray = null;

    String convertedString = DTOMapper.INSTANCE.byteArrayToString(byteArray);

    assertNull(convertedString);
  }
  
}