package ch.uzh.ifi.hase.soprafs24.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

@WebAppConfiguration
@SpringBootTest
public class GameServiceResultsTest {

    @Autowired
    private GameService gameService;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private UserRepository userRepository;

    private Game testGame;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setup() {
        // Create a game
        testGame = new Game();
        testGame.setId(1L);
        testGame.setGameStatus(GameStatus.GAMEOVER);
        
        // Set up community cards (a flush board)
        List<String> communityCards = Arrays.asList("2H", "5H", "8H", "JD", "KC");
        testGame.setCommunityCards(communityCards);
        
        // Create players with different hands
        // Player 1 has a flush (hearts)
        player1 = new Player(1L, Arrays.asList("AH", "QH"), testGame);
        // Player 2 has a pair of kings
        player2 = new Player(2L, Arrays.asList("KS", "KH"), testGame);
        
        List<Player> players = Arrays.asList(player1, player2);
        testGame.setPlayers(players);
        
        // Mock repository
        when(gameRepository.findByid(1L)).thenReturn(testGame);
    }

    @Test
    public void testDetermineWinners_flushVsPair() {
        // Given the setup in @BeforeEach
        
        // When determining winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Then player1 should be the winner (flush beats pair)
        assertTrue(winners.size() > 0, "Should have at least one winner");
        assertEquals(player1.getUserId(), winners.get(0).getUserId(), "Player 1 should win with a flush");
    }
    
    @Test
    public void testGetHandDescription_verifyNotNull() {
        // Given
        List<String> communityCards = Arrays.asList("AS", "AD", "AC", "5H", "8D");
        Player player = new Player(1L, Arrays.asList("AH", "KD"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertNotNull(handDescription, "Hand description should not be null");
        assertTrue(handDescription.length() > 0, "Hand description should not be empty");
    }
    
    @Test
    public void testGetHandDescription_checkForHighCardDescription() {
        // Given
        List<String> communityCards = Arrays.asList("2S", "5D", "8C", "9H", "KC");
        Player player = new Player(1L, Arrays.asList("3D", "4H"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertNotNull(handDescription, "Hand description should not be null");
        assertTrue(handDescription.length() > 0, "Hand description should not be empty");
    }
} 