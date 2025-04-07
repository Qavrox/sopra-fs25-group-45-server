package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

public class Authenticator {

    UserRepository userRepository;
    GameRepository gameRepository;

    public Authenticator(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public void checkTokenValidity( String token) {
        // Check if the token is valid
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        
        for( User user : userRepository.findAll()) {
            if (user.getToken() != null && user.getToken().equals(token)) {
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");

    }
    
}
