package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerActionPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ProbabilityResponse;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameResultsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStatisticsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PokerAdviceResponseDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.List;

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
            if (player.getUserId().equals(playerActionDTO.getUserId())) {
                isUserPlayer = true;
                break;
            }
        }

        if (!isUserPlayer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only perform actions for yourself");
        }

        Game result = gameService.processPlayerAction(
                gameId,
                playerActionDTO.getUserId(),
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

    @GetMapping("/games/{gameId}/results")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameResultsDTO getGameResults(
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

        // Check if game is finished
        if (game.getGameStatus() != GameStatus.GAMEOVER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is not finished yet");
        }

        // Get winners
        List<Player> winners = gameService.determineWinners(gameId);
        if (winners.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No winners found");
        }

        // Create response
        GameResultsDTO response = new GameResultsDTO();
        Player winner = winners.get(0);
        response.setWinner(winner);
        
        // Get winning hand description
        String handDescription = gameService.getHandDescription(winner, game.getCommunityCards());
        response.setWinningHand(handDescription);
        
        // Set statistics
        GameStatisticsDTO statistics = new GameStatisticsDTO();
        // Calculate participation rate (number of active players / total players)
        int activePlayers = 0;
        for (Player player : game.getPlayers()) {
            if (!player.getHasFolded()) {
                activePlayers++;
            }
        }
        double participationRate = (double) activePlayers / game.getPlayers().size();
        statistics.setParticipationRate(participationRate);
        
        // For now, set pots won to 1 since we don't track this yet
        statistics.setPotsWon(1);
        response.setStatistics(statistics);

        return response;
    }
    
    /**
     * Get AI-generated poker advice for the current game state
     */
    @GetMapping("/games/{gameId}/poker-advice")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PokerAdviceResponseDTO getPokerAdvice(
            @PathVariable("gameId") Long gameId,
            @RequestHeader("Authorization") String authenticatorToken) {
        
        String token = authenticatorToken.substring(7);
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Get advice from Gemini API
        String advice = gameService.getPokerAdvice(gameId, user.getId());
        
        // Create and return response
        PokerAdviceResponseDTO response = new PokerAdviceResponseDTO();
        response.setAdvice(advice);
        
        return response;
    }
}