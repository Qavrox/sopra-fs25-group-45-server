package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

public class AuthenticatorTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private Authenticator authenticator;

    private User user;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    
        // Initialize the Authenticator with mocked repositories
        authenticator = new Authenticator(userRepository, gameRepository);

        // Set up user
        user= new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(java.time.LocalDate.now());
        user.setToken("valid-token");    


        // Mocks
        when(userRepository.findAll()).thenReturn(List.of(user));

    }

    @Test
    void testCheckTokenValidityValidToken(){

        assertDoesNotThrow(() -> {
            authenticator.checkTokenValidity(user.getToken());
        });
    }

    @Test 
    void testCheckTokenValidityInvalidToken(){
        assertThrows(ResponseStatusException.class, () -> {
            authenticator.checkTokenValidity("invalid-token");
        });

        assertThrows(ResponseStatusException.class, () -> {
            authenticator.checkTokenValidity(null);
        });
        assertThrows(ResponseStatusException.class, () -> {
            authenticator.checkTokenValidity("");
        });
    }

    
}
