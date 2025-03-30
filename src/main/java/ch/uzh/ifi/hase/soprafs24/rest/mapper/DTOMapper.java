package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreationPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.JoinGamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;y
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "profileImage", target = "profileImage")
  LoginResponseDTO convertEntityToLoginResponseDTO(User user);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "profileImage", target = "profileImage")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "profileImage", target = "profileImage")
  @Mapping(source = "level", target = "level")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "isPublic", target = "isPublic")
  @Mapping(source = "maximalPlayers", target = "maximalPlayers")
  @Mapping(source = "startCredit", target = "startCredit")
  Game convertCreateGameDTOToGameEntity(GameCreationPostDTO gamePostDTO);

  @Mapping(source = "isPublic", target = "isPublic")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "maximalPlayers", target = "maximalPlayers")
  @Mapping(source = "startCredit", target = "startCredit")
  GameGetDTO convertEntityToGameGetDTO(Game game);



  @Mapping(source = "id", target = "id")
  // TODO: fix mapping
  UserFriendDTO convertEntityToUserFriendDTO(User user);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "profileImage", target = "profileImage")
  @Mapping(source = "level", target = "level")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

  List<UserGetDTO> convertEntityListToUserGetDTOList(List<User> users);
}
