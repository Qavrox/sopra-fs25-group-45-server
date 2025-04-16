package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerActionPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ProbabilityResponse;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.User;

@RestController
public class GameActionController {

    private final GameService gameService;
    private final UserService userService;

    GameActionController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/games/{gameId}/start-betting")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO startBettingRound(@PathVariable("gameId") Long gameId, @RequestHeader("Authorization") String authenticatorToken) {
        String token = authenticatorToken.substring(7);
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Game game = gameService.getGameById(gameId, token);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        if (!user.getId().equals(game.getCreatorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the host can start the game");
        }

        game = gameService.startBettingRound(gameId);
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
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Game game = gameService.getGameById(gameId, token);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        boolean isUserPlayer = false;
        for (Player player : game.getPlayers()) {
            if (player.getId().equals(playerActionDTO.getPlayerId()) && 
                player.getUserId().equals(user.getId())) {
                isUserPlayer = true;
                break;
            }
        }

        if (!isUserPlayer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only perform actions for yourself");
        }

        Game result = gameService.processPlayerAction(
                gameId,
                playerActionDTO.getPlayerId(),
                playerActionDTO.getAction(),
                playerActionDTO.getAmount());
        
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(result);
        return gameGetDTO;

    }

    @GetMapping("/games/{gameId}/probability")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ProbabilityResponse getWinProbability(
            @PathVariable("gameId") Long gameId,
            @RequestHeader("Authorization") String authenticatorToken) {
        
        String token = authenticatorToken.substring(7);
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Game game = gameService.getGameById(gameId, token);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if user is a player in the game
        boolean isUserPlayer = false;
        for (Player player : game.getPlayers()) {
            if (player.getUserId().equals(user.getId())) {
                isUserPlayer = true;
                break;
            }
        }

        if (!isUserPlayer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must be a player in the game to get probability");
        }

        double probability = gameService.calculateWinProbability(gameId, user.getId());
        ProbabilityResponse response = new ProbabilityResponse();
        response.setProbability(probability);
        return response;
    }
}