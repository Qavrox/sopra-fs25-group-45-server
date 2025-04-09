package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private List<Player> players;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Setup a basic game
        game = new Game();
        game.setId(1L);
        game.setGameStatus(GameStatus.RIVER);
        game.setMaximalPlayers(5);
        game.setStartCredit(1000L);
        game.setCreatorId(1);
        game.setIsPublic(true);
        
        // Initialize cardDeck and communityCards
        game.setCardDeck(new ArrayList<>());
        game.setCommunityCards(new ArrayList<>());
        
        // Add players
        players = new ArrayList<>();
        Player player1 = new Player(1L, new ArrayList<>(), game);
        Player player2 = new Player(2L, new ArrayList<>(), game);
        Player player3 = new Player(3L, new ArrayList<>(), game);
        
        players.add(player1);
        players.add(player2);
        players.add(player3);
        
        // Add players to game
        for (Player player : players) {
            game.addPlayer(player);
        }
        
        // Setup mocks
        when(gameRepository.findByid(1L)).thenReturn(game);
        when(gameRepository.findByid(2L)).thenReturn(null);
    }

    @Test
    void testDetermineWinners_NotEnoughCommunityCards() {
        // Setup with only 4 community cards
        List<String> communityCards = Arrays.asList("AH", "KH", "QH", "JH");
        game.setCommunityCards(communityCards);
        
        // Verify that an exception is thrown when there are not enough community cards
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.determineWinners(1L);
        });
        
        assertTrue(exception.getMessage().contains("Not enough community cards"));
    }

    @Test
    void testDetermineWinners_GameNotFound() {
        // Verify that an exception is thrown when the game is not found
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.determineWinners(2L);
        });
        
        assertTrue(exception.getMessage().contains("Game not found"));
    }
    
    @Test
    void testDetermineWinners_RoyalFlushWins() {
        // Setup community cards for a royal flush scenario
        List<String> communityCards = Arrays.asList("10S", "JS", "QS", "KS", "5H");
        game.setCommunityCards(communityCards);
        
        // Setup player hands
        players.get(0).setHand(Arrays.asList("AS", "2H")); // Royal flush (A-K-Q-J-10 of Spades)
        players.get(1).setHand(Arrays.asList("AH", "AD")); // Pair of Aces
        players.get(2).setHand(Arrays.asList("2S", "2D")); // Pair of 2s
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that only the player with the royal flush wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }
    
    @Test
    void testDetermineWinners_FullHouseBeatsFlush() {
        // Setup community cards
        List<String> communityCards = Arrays.asList("2S", "2C", "2D", "KH", "QH");
        game.setCommunityCards(communityCards);
        
        // Setup player hands - create clearly distinguishable hands
        players.get(0).setHand(Arrays.asList("KC", "KD")); // Full house: three 2s and a pair of Kings
        players.get(1).setHand(Arrays.asList("AH", "JH")); // Flush in hearts (A, K, Q, J hearts + one more)
        players.get(2).setHand(Arrays.asList("3H", "4H")); // Flush in hearts (lower than player 1)
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that only player with full house wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }
    
    @Test
    void testDetermineWinners_TieBreakerWithKicker() {
        // Setup community cards
        List<String> communityCards = Arrays.asList("AH", "AD", "3H", "4D", "5H");
        game.setCommunityCards(communityCards);
        
        // Setup player hands with the same pair but different kickers
        players.get(0).setHand(Arrays.asList("KH", "QH")); // Pair of Aces with K,Q kickers
        players.get(1).setHand(Arrays.asList("KD", "JD")); // Pair of Aces with K,J kickers
        players.get(2).setHand(Arrays.asList("QD", "JH")); // Pair of Aces with Q,J kickers
        
        // Get the winners
        List<Player> winners = gameService.determineWinners(1L);
        
        // Verify that player with the highest kicker wins
        assertEquals(1, winners.size());
        assertEquals(1L, winners.get(0).getUserId());
    }
} 