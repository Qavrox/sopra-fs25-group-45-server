package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreationPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void testGetGameFromUserPostDTOToGameEntity() {

        
    GameCreationPostDTO gamePostDTO = new GameCreationPostDTO();
    gamePostDTO.setCreatorId(1);
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
}
