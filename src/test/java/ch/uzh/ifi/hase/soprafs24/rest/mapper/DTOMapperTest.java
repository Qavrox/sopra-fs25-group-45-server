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

    //when LoginResponseDTO is created
    LoginResponseDTO loginResponseDTO = DTOMapper.INSTANCE.convertEntityToLoginResponseDTO(user);

    //then
    assertEquals(user.getToken(), loginResponseDTO.getToken());
    assertEquals(user.getName(), (loginResponseDTO.getUser()).getDisplayName());
    assertEquals(user.getUsername(), loginResponseDTO.getUser().getUsername());
    assertEquals(user.getProfileImage(), (loginResponseDTO.getUser()).getAvatarUrl());
    assertEquals(user.getCreationDate(), (loginResponseDTO.getUser()).getCreatedAt());
    assertEquals(user.getBirthday(), (loginResponseDTO.getUser()).getBirthday());
    assertEquals(user.getexperienceLevel(), (loginResponseDTO.getUser()).getExperienceLevel());
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
    assertEquals(user.getCreationDate(), userFriendDTO.getCreatedAt());
    assertEquals(user.getBirthday(), userFriendDTO.getBirthday());
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
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    //when UserProfileDTO is created
    UserProfileDTO userProfileDTO = DTOMapper.INSTANCE.convertEntityToUserProfileDTO(user);

    //then
    assertEquals(user.getId(), userProfileDTO.getId());
    assertEquals(user.getName(), userProfileDTO.getDisplayName());
    assertEquals(user.getUsername(), userProfileDTO.getUsername());
    assertEquals(user.getProfileImage(), userProfileDTO.getAvatarUrl());
    assertEquals(user.getexperienceLevel(), userProfileDTO.getExperienceLevel());
    assertEquals(user.getBirthday(), userProfileDTO.getBirthday());
    assertEquals(user.getCreationDate(), userProfileDTO.getCreatedAt());
    assertEquals(user.getStatus() == UserStatus.ONLINE, userProfileDTO.isOnline());
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