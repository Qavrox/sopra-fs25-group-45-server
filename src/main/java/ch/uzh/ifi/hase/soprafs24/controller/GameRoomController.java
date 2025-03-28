
package ch.uzh.ifi.hase.soprafs24.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;

@RestController
public class GameRoomController {

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createAGame(@RequestBody GamePostDTO gamePostDTO){

        Game newGame = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        

        return null;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllPublicGames(@RequestBody GameGetDTO gameGetDTO){

        return null;
    }

    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable("gameId") Long id, @RequestBody GameGetDTO gameGetDTO){

        return null;
    }

    @PostMapping("/games/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable("gameId") Long id, @RequestBody GamePostDTO gamePostDTO){

        return null;
    }    

    
}
