package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerActionPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

@RestController
public class GameActionController {

    private final GameService gameService;

    GameActionController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games/{gameId}/start-betting")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO startBettingRound(@PathVariable("gameId") Long gameId, @RequestHeader("Authorization") String authenticatorToken) {
        String token = authenticatorToken.substring(7);
        Game game = gameService.startBettingRound(gameId);
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        return gameGetDTO;
    }

    @PostMapping("/games/{gameId}/action")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO performPlayerAction(
            @PathVariable("gameId") Long gameId,
            @RequestBody PlayerActionPostDTO playerActionDTO,
            @RequestHeader("Authorization") String authenticatorToken) {
        
        String token = authenticatorToken.substring(7);
        
        Game game = gameService.processPlayerAction(
                gameId,
                playerActionDTO.getPlayerId(),
                playerActionDTO.getAction(),
                playerActionDTO.getAmount());
        
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        return gameGetDTO;

    }
}