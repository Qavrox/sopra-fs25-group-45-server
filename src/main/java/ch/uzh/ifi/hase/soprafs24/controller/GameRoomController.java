
package ch.uzh.ifi.hase.soprafs24.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameCreationPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.JoinGamePostDTO;
import ch.uzh.ifi.hase.soprafs24.service.Authenticator;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;

@RestController
public class GameRoomController {


    private final GameService gameService;

    GameRoomController(GameService gameService) {
      this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createAGame(@RequestHeader("Authorization") String authenticatorToken, @RequestBody GameCreationPostDTO gamePostDTO){

        String token = authenticatorToken.substring(7);
        // Check if the token is valid
        Game newGame = DTOMapper.INSTANCE.convertCreateGameDTOToGameEntity(gamePostDTO);
        gameService.createNewGame(newGame, token);
        GameGetDTO newGameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);

        return newGameGetDTO;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllPublicGames(@RequestHeader("Authorization") String authenticatorToken){

        String token = authenticatorToken.substring(7);
        List<Game> allGames = gameService.getAllPublicGames(token);
        List<GameGetDTO> allGamesGetDTO = new ArrayList<>();
        for (Game game : allGames) {
            System.out.println(game.getId());
            GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
            allGamesGetDTO.add(gameGetDTO);
        }
        return allGamesGetDTO;
    }

    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable("gameId") Long id, @RequestHeader("Authorization") String authenticatorToken){

        String token = authenticatorToken.substring(7);
        Game foundGame = gameService.getGameById(id, token);
        GameGetDTO foundGameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(foundGame);

        return foundGameGetDTO;
    }

    @PostMapping("/games/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO joinGame(@PathVariable("gameId") Long id, @RequestBody JoinGamePostDTO gamePostDTO, @RequestHeader("Authorization") String authenticatorToken){
        String token = authenticatorToken.substring(7);

        Game game = gameService.joinGame(id, token, gamePostDTO.getPassword());
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        return gameGetDTO;


    }    

    
}
