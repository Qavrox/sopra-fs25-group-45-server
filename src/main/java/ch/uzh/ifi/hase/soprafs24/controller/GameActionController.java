package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerActionPostDTO;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/games/{gameId}/action")
public class GameActionController {

    private final GameService gameService;
    private final UserRepository userRepository;

    @Autowired
    public GameActionController(GameService gameService, UserRepository userRepository) {
        this.gameService = gameService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void performPlayerAction(@PathVariable("gameId") Long gameId,
                                    @RequestHeader("Authorization") String authenticatorToken,
                                    @RequestBody PlayerActionPostDTO playerActionDTO) {

        String token = authenticatorToken.substring(7); // Strip "Bearer "

        // Validate token and find user
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Game game = gameService.getGameById(gameId, token);

        // Find matching player
        Player player = game.getPlayers().stream()
                .filter(p -> p.getUserId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a player in this game"));

        // Call main service method
        gameService.processPlayerAction(gameId, player.getId(), playerActionDTO.getAction(), playerActionDTO.getAmount());
    }
}