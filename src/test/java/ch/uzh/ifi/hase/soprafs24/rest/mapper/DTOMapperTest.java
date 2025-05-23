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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    gamePostDTO.setCreatorId(1L);
    gamePostDTO.setPublic(true);
    gamePostDTO.setMaximalPlayers(5);
    gamePostDTO.setStartCredit(1000);
    gamePostDTO.setSmallBlind(10);
    gamePostDTO.setBigBlind(20);


    // MAP -> Create user
    Game game = DTOMapper.INSTANCE.convertCreateGameDTOToGameEntity(gamePostDTO);

    // check content
    assertEquals(gamePostDTO.getCreatorId(), game.getCreatorId());
    assertEquals(gamePostDTO.getIsPublic(), game.getIsPublic());
    assertEquals(gamePostDTO.getMaximalPlayers(), game.getMaximalPlayers());
    assertEquals(gamePostDTO.getStartCredit(), game.getStartCredit());
    assertEquals(gamePostDTO.getSmallBlind(), game.getSmallBlind());
    assertEquals(gamePostDTO.getBigBlind(), game.getBigBlind());
  }

  @Test
  public void testEntityToGameGetDTO() {
    // create Game
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

    // Create GameGetDTO
    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    // check content
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
    //given User
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setProfileImage(1);
    user.setBirthday(LocalDate.of(1995, 5, 5));
    user.setCreationDate(LocalDate.now());
    user.setexperienceLevel(UserLevel.Beginner);

    //when LoginResponseDTO is created
    LoginResponseDTO loginResponseDTO = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(user);

    //then
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

    // Test with null token and other optional fields
    User userWithNulls = new User();
    userWithNulls.setId(2L);
    userWithNulls.setName("Another User");
    userWithNulls.setUsername("anotheruser");
    userWithNulls.setStatus(UserStatus.OFFLINE);
    // token is null by default
    // profileImage, birthday, creationDate, experienceLevel are null by default

    LoginResponseDTO loginResponseDTONulls = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(userWithNulls);

    assertNull(loginResponseDTONulls.getToken());
    assertNotNull(loginResponseDTONulls.getUser());
    assertEquals(userWithNulls.getId(), (loginResponseDTONulls.getUser()).getId());
    assertEquals(userWithNulls.getName(), (loginResponseDTONulls.getUser()).getDisplayName());
    assertEquals(userWithNulls.getUsername(), (loginResponseDTONulls.getUser()).getUsername());
    assertEquals(0, (loginResponseDTONulls.getUser()).getAvatarUrl()); // Expect 0 if profileImage is default int (0)
    assertNull((loginResponseDTONulls.getUser()).getCreatedAt());
    assertNull((loginResponseDTONulls.getUser()).getBirthday());
    assertNull((loginResponseDTONulls.getUser()).getExperienceLevel());
    assertFalse((loginResponseDTONulls.getUser()).isOnline());
  }

  @Test
  public void testUserFriendDTOfromEntity() {
    //given User
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    //when UserFriendDTO is created
    UserFriendDTO userFriendDTO = DTOMapper.INSTANCE.convertEntityToUserFriendDTO(user);

    //then
    assertEquals(user.getId(), userFriendDTO.getId());
    assertEquals(user.getUsername(), userFriendDTO.getUsername());
    assertEquals(user.getStatus(), userFriendDTO.getOnline());
    assertNull(userFriendDTO.getCreatedAt());
    assertNull(userFriendDTO.getBirthday());
  }

  @Test
  public void testUserfromUserPutDTOtoEntity() {
    //given UserPutDTO
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setName("Firstname Lastname");
    userPutDTO.setUsername("firstname@lastname");
    userPutDTO.setexperienceLevel(UserLevel.Beginner);
    userPutDTO.setPassword("password");
    userPutDTO.setBirthday(LocalDate.of(1990, 1, 1));
    userPutDTO.setProfileImage(1);


    //when User is created
    User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

    //then
    assertEquals(user.getName(), userPutDTO.getName());
    assertEquals(user.getUsername(), userPutDTO.getUsername());
    assertEquals(user.getexperienceLevel(), userPutDTO.getexperienceLevel());
    assertEquals(user.getPassword(), userPutDTO.getPassword());
    assertEquals(user.getBirthday(), userPutDTO.getBirthday());
    assertEquals(user.getProfileImage(), userPutDTO.getProfileImage());
  }

  @Test
  public void testUserProfileDTOfromEntity() {
    //given User
    User user = new User();
    user.setId(1L); // Set ID for completeness
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.ONLINE); // Test with ONLINE status
    user.setToken("1");
    user.setProfileImage(2);
    user.setexperienceLevel(UserLevel.Intermediate);
    user.setBirthday(LocalDate.of(1990, 1, 1));
    user.setCreationDate(LocalDate.of(2024, 1, 1));

    //when UserProfileDTO is created
    UserProfileDTO userProfileDTO = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(user);

    //then
    assertEquals(user.getId(), userProfileDTO.getId());
    assertEquals(user.getName(), userProfileDTO.getDisplayName());
    assertEquals(user.getUsername(), userProfileDTO.getUsername());
    assertEquals(user.getProfileImage(), userProfileDTO.getAvatarUrl());
    assertEquals(user.getexperienceLevel().toString(), userProfileDTO.getExperienceLevel().toString());
    assertEquals(user.getBirthday(), userProfileDTO.getBirthday());
    assertEquals(user.getCreationDate().atStartOfDay(), userProfileDTO.getCreatedAt());
    assertTrue(userProfileDTO.isOnline());

    // Test with OFFLINE status and null optional values
    User userOfflineNulls = new User();
    userOfflineNulls.setId(2L);
    userOfflineNulls.setName("Another One");
    userOfflineNulls.setUsername("anotherone");
    userOfflineNulls.setStatus(UserStatus.OFFLINE);
    // profileImage, experienceLevel, birthday, creationDate are null

    UserProfileDTO userProfileDTOOfflineNulls = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(userOfflineNulls);

    assertEquals(userOfflineNulls.getId(), userProfileDTOOfflineNulls.getId());
    assertEquals(userOfflineNulls.getName(), userProfileDTOOfflineNulls.getDisplayName());
    assertEquals(userOfflineNulls.getUsername(), userProfileDTOOfflineNulls.getUsername());
    assertEquals(0, userProfileDTOOfflineNulls.getAvatarUrl()); // Expect 0 if profileImage is default int (0)
    assertNull(userProfileDTOOfflineNulls.getExperienceLevel());
    assertNull(userProfileDTOOfflineNulls.getBirthday());
    assertNull(userProfileDTOOfflineNulls.getCreatedAt());
    assertFalse(userProfileDTOOfflineNulls.isOnline());
  }

  @Test
  public void testGameHistoryDTOfromEntity() {
    //given GameHistory
    GameHistory gameHistory = new GameHistory();
    gameHistory.setId(1L);
    gameHistory.setGameId(1L);
    gameHistory.setUserId(1L);
    gameHistory.setResult("Win");
    gameHistory.setWinnings(100L);
    gameHistory.setOtherPlayerIds(List.of(2L, 3L));
    gameHistory.setPlayedAt(LocalDateTime.now());

    //when GameHistoryDTO is created
    GameHistoryDTO gameHistoryDTO = DTOMapper.INSTANCE.convertEntityToGameHistoryDTO(gameHistory);

    //then
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
    //given GameHistoryDTO
    GameHistoryDTO gameHistoryDTO = new GameHistoryDTO();
    gameHistoryDTO.setId(1L);
    gameHistoryDTO.setGameId(1L);
    gameHistoryDTO.setUserId(1L);
    gameHistoryDTO.setResult("Win");
    gameHistoryDTO.setWinnings(100L);
    gameHistoryDTO.setOtherPlayerIds(List.of(2L, 3L));
    gameHistoryDTO.setPlayedAt(LocalDateTime.now());

    //when GameHistory is created
    GameHistory gameHistory = DTOMapper.INSTANCE.convertGameHistoryDTOtoEntity(gameHistoryDTO);

    //then
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
    // create Users
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

    // MAP -> Create List<UserGetDTO>
    List<UserGetDTO> userGetDTOs = DTOMapper.INSTANCE.convertEntityListToUserGetDTOList(users);

    // check content
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
}

/* 


    LoginResponseDTO convertEntityToLoginResponseDTO(User user); DONE

    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO); DONE

    UserGetDTO convertEntityToUserGetDTO(User user); DONE

    Game convertCreateGameDTOToGameEntity(GameCreationPostDTO gamePostDTO); DONE

    GameGetDTO convertEntityToGameGetDTO(Game game); DONE

    UserFriendDTO convertEntityToUserFriendDTO(User user); DONE

    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO); DONE


    UserProfileDTO convertEntityToUserProfileDTO(User user); DONE


    GameHistoryDTO convertEntityToGameHistoryDTO(GameHistory gameHistory); DONE

   
    GameHistory convertGameHistoryDTOtoEntity(GameHistoryDTO gameHistoryDTO); DONE
}
    */