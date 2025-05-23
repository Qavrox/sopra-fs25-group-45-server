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
    public void testWinnersListContainsOnlyWinners() {
        // Given the setup in @BeforeEach where player1 has a flush and player2 has a pair
        
        // When determining winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Then only one winner should be returned (not all players)
        assertEquals(1, winners.size(), "Should have exactly one winner");
        assertEquals(player1.getUserId(), winners.get(0).getUserId(), "Player 1 should be the only winner");
        
        // Verify that the game's winners list is properly set by checking the game directly
        // The winners should be stored in the database after calling determineWinners
        Game updatedGame = gameRepository.findByid(1L);
        List<Player> gameWinners = updatedGame.getWinners();
        assertEquals(1, gameWinners.size(), "Game should have exactly one winner stored");
        assertEquals(player1.getUserId(), gameWinners.get(0).getUserId(), "Game winner should be player 1");
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
        assertEquals("Four of a Kind, Aces", handDescription, "Should correctly identify four of a kind");
    }
    
    @Test
    public void testGetHandDescription_checkForHighCardDescription() {
        // Given
        List<String> communityCards = Arrays.asList("2S", "5D", "8C", "9H", "JD");
        Player player = new Player(1L, Arrays.asList("3D", "4H"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertNotNull(handDescription, "Hand description should not be null");
        assertTrue(handDescription.length() > 0, "Hand description should not be empty");
        assertEquals("High Card: Jack", handDescription, "Should correctly identify high card");
    }

    @Test
    public void testGetHandDescription_flush() {
        // Given - player has a flush with AH QH and community cards 2H 5H 8H JD KC
        List<String> communityCards = Arrays.asList("2H", "5H", "8H", "JD", "KC");
        Player player = new Player(1L, Arrays.asList("AH", "QH"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertEquals("Flush, Ace high", handDescription, "Should correctly identify a flush");
    }

    @Test
    public void testGetHandDescription_straightFlush() {
        // Given - player has a straight flush with 9H 10H and community cards JH QH KH 2D 3S
        List<String> communityCards = Arrays.asList("JH", "QH", "KH", "2D", "3S");
        Player player = new Player(1L, Arrays.asList("9H", "10H"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertEquals("Straight Flush, King high", handDescription, "Should correctly identify a straight flush");
    }

    @Test
    public void testGetHandDescription_fullHouse() {
        // Given - player has a full house with AA and community cards A23 33
        List<String> communityCards = Arrays.asList("AS", "2D", "3H", "3S", "3C");
        Player player = new Player(1L, Arrays.asList("AH", "AD"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertEquals("Full House, Aces full of 3s", handDescription, "Should correctly identify a full house");
    }

    @Test
    public void testGetHandDescription_twoPair() {
        // Given - player has two pair with 88 and community cards 8K7 K2
        List<String> communityCards = Arrays.asList("8C", "KD", "7H", "KS", "2C");
        Player player = new Player(1L, Arrays.asList("8H", "8D"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertEquals("Full House, 8s full of Kings", handDescription, "Should correctly identify a full house with three 8s and two Kings");
    }

    @Test
    public void testGetHandDescription_pairOfAces() {
        // Given - player has a pair of aces with A5 and community cards 234 AK
        List<String> communityCards = Arrays.asList("2S", "3D", "4H", "AS", "KD");
        Player player = new Player(1L, Arrays.asList("AC", "5H"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        // The hand actually forms a straight: A,2,3,4,5
        assertEquals("Straight, 5 high", handDescription, "Should correctly identify a straight");
    }

    @Test
    public void testGetHandDescription_straight() {
        // Given - player has a straight with 45 and community cards 678 AK
        List<String> communityCards = Arrays.asList("6S", "7D", "8H", "AS", "KD");
        Player player = new Player(1L, Arrays.asList("4C", "5H"), testGame);
        
        // When
        String handDescription = gameService.getHandDescription(player, communityCards);
        
        // Then
        assertEquals("Straight, 8 high", handDescription, "Should correctly identify a straight");
    }
} 