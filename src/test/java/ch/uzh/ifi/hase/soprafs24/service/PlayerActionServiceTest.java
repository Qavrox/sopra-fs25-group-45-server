package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlayerActionServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create a test game
        testGame = new Game();
        testGame.setId(1L);
        testGame.setCreatorId(1);
        testGame.setIsPublic(true);
        testGame.setMaximalPlayers(6);
        testGame.setStartCredit(1000L);
        testGame.setGameStatus(GameStatus.READY);
        testGame.setPot(0L);
        testGame.setCallAmount(0L);
        testGame.initializeShuffledDeck();
        testGame.setCommunityCards(new ArrayList<>());

        // Create test players
        List<String> hand1 = new ArrayList<>();
        hand1.add("AH");
        hand1.add("KH");
        player1 = new Player(1L, hand1, testGame);
        player1.setId(1L);

        List<String> hand2 = new ArrayList<>();
        hand2.add("QS");
        hand2.add("JS");
        player2 = new Player(2L, hand2, testGame);
        player2.setId(2L);

        List<String> hand3 = new ArrayList<>();
        hand3.add("10C");
        hand3.add("9C");
        player3 = new Player(3L, hand3, testGame);
        player3.setId(3L);

        // Add players to game
        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
        testGame.addPlayer(player3);

        // Set blinds
        testGame.setStartBlinds();

        // Configure mocks
        when(gameRepository.findByid(1L)).thenReturn(testGame);
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(playerRepository.save(any(Player.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testStartBettingRound() {
        // Test starting a betting round
        Game result = gameService.startBettingRound(1L);

        // Verify game status changed to PREFLOP
        assertEquals(GameStatus.PREFLOP, result.getGameStatus());

        // Verify small blind was placed
        Player smallBlindPlayer = result.getPlayers().get(result.getSmallBlindIndex());
        assertTrue(smallBlindPlayer.getCurrentBet() > 0);

        // Verify big blind was placed
        Player bigBlindPlayer = result.getPlayers().get(result.getBigBlindIndex());
        assertTrue(bigBlindPlayer.getCurrentBet() > smallBlindPlayer.getCurrentBet());

        // Verify current player is after big blind
        assertEquals((result.getBigBlindIndex() + 1) % result.getPlayers().size(), result.getCurrentPlayerIndex());
    }

    @Test
    public void testPlayerActionCheck() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // All players have equal bets (0 in this case)
        for (Player p : testGame.getPlayers()) {
            p.setCurrentBet(0L);
        }

        // Player checks
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);

        // Verify player action was recorded
        Player player = result.getPlayers().get(0);
        assertTrue(player.getHasActed());
        assertEquals(PlayerAction.CHECK, player.getLastAction());
        
        // Verify next player's turn
        assertEquals(1, result.getCurrentPlayerIndex());
    }

    @Test
    public void testPlayerActionCall() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set up a bet to call
        player2.setCurrentBet(50L);
        testGame.setCallAmount(50L);

        // Initial credit
        Long initialCredit = player1.getCredit();

        // Player calls
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CALL, 0L);

        // Verify player action was recorded
        Player player = result.getPlayers().get(0);
        assertTrue(player.getHasActed());
        assertEquals(PlayerAction.CALL, player.getLastAction());
        assertEquals(50L, player.getCurrentBet());
        assertEquals(initialCredit - 50L, player.getCredit());
        
        // Verify next player's turn
        assertEquals(1, result.getCurrentPlayerIndex());
    }

    @Test
    public void testPlayerActionBet() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Initial credit
        Long initialCredit = player1.getCredit();
        Long betAmount = 100L;

        // Player bets
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.BET, betAmount);

        // Verify player action was recorded
        Player player = result.getPlayers().get(0);
        assertTrue(player.getHasActed());
        assertEquals(PlayerAction.BET, player.getLastAction());
        assertEquals(betAmount, player.getCurrentBet());
        assertEquals(initialCredit - betAmount, player.getCredit());
        
        // Verify call amount was updated
        assertEquals(betAmount, result.getCallAmount());
        
        // Verify next player's turn
        assertEquals(1, result.getCurrentPlayerIndex());
    }

    @Test
    public void testPlayerActionRaise() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set up a bet to raise
        player3.setCurrentBet(50L);
        testGame.setCallAmount(50L);

        // Initial credit
        Long initialCredit = player1.getCredit();
        Long raiseAmount = 150L;

        // Player raises
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.RAISE, raiseAmount);

        // Verify player action was recorded
        Player player = result.getPlayers().get(0);
        assertTrue(player.getHasActed());
        assertEquals(PlayerAction.RAISE, player.getLastAction());
        assertEquals(raiseAmount, player.getCurrentBet());
        assertEquals(initialCredit - raiseAmount, player.getCredit());
        
        // Verify call amount was updated
        assertEquals(raiseAmount, result.getCallAmount());
        
        // Verify next player's turn
        assertEquals(1, result.getCurrentPlayerIndex());
    }

    @Test
    public void testPlayerActionFold() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);

        // Player folds
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.FOLD, 0L);

        // Verify player action was recorded
        Player player = result.getPlayers().get(0);
        assertTrue(player.getHasActed());
        assertTrue(player.getHasFolded());
        assertEquals(PlayerAction.FOLD, player.getLastAction());
        
        // Verify next player's turn
        assertEquals(1, result.getCurrentPlayerIndex());
    }

    @Test
    public void testBettingRoundCompletion() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // All players have equal bets and have acted
        for (Player p : testGame.getPlayers()) {
            p.setCurrentBet(10L);
            p.setHasActed(true);
        }
        
        // Set one player to not have acted yet
        player1.setHasActed(false);
        testGame.setCurrentPlayerIndex(0);

        // Last player acts
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);

        // Verify betting round is complete and game moved to FLOP
        assertEquals(GameStatus.FLOP, result.getGameStatus());
        
        // Verify community cards were dealt
        assertEquals(3, result.getCommunityCards().size());
        
        // Verify bets were collected into pot
        assertEquals(30L, result.getPot());
        
        // Verify player bets were reset
        for (Player p : result.getPlayers()) {
            assertEquals(0L, p.getCurrentBet());
            assertFalse(p.getHasActed());
        }
    }

    @Test
    public void testAllInScenario() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set player with low chips
        player1.setCredit(50L);
        
        // Another player has bet more
        player2.setCurrentBet(100L);
        testGame.setCallAmount(100L);

        // Player goes all-in by calling
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CALL, 0L);

        // Verify player went all-in
        Player player = result.getPlayers().get(0);
        assertEquals(0L, player.getCredit());
        assertEquals(50L, player.getCurrentBet());
        assertEquals(PlayerAction.CALL, player.getLastAction());
    }

    @Test
    public void testInvalidActionCheck() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set up a bet that prevents checking
        player2.setCurrentBet(50L);
        testGame.setCallAmount(50L);

        // Player tries to check when there's a bet
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        });
        
        assertTrue(exception.getMessage().contains("Cannot check when there are bets"));
    }

    @Test
    public void testInvalidActionBet() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set up a bet that prevents betting (must raise instead)
        player2.setCurrentBet(50L);
        testGame.setCallAmount(50L);

        // Player tries to bet when there's already a bet
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.processPlayerAction(1L, 1L, PlayerAction.BET, 100L);
        });
        
        assertTrue(exception.getMessage().contains("Cannot bet when there are already bets"));
    }

    @Test
    public void testInvalidActionRaise() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Set up a bet
        player2.setCurrentBet(50L);
        testGame.setCallAmount(50L);

        // Player tries to raise with an amount less than current bet
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.processPlayerAction(1L, 1L, PlayerAction.RAISE, 40L);
        });
        
        assertTrue(exception.getMessage().contains("Raise amount must be higher than current highest bet"));
    }

    @Test
    public void testNotPlayerTurn() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(1); // Player 2's turn

        // Player 1 tries to act when it's not their turn
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        });
        
        assertTrue(exception.getMessage().contains("It's not your turn"));
    }

    @Test
    public void testGamePhaseProgression() {
        // Test progression through all game phases
        
        // Start with PREFLOP
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // All players have equal bets and have acted
        for (Player p : testGame.getPlayers()) {
            p.setCurrentBet(10L);
            p.setHasActed(true);
        }
        
        // Set one player to not have acted yet
        player1.setHasActed(false);
        testGame.setCurrentPlayerIndex(0);

        // Complete PREFLOP -> FLOP
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        assertEquals(GameStatus.FLOP, result.getGameStatus());
        assertEquals(3, result.getCommunityCards().size());
        
        // Reset for FLOP -> TURN
        for (Player p : result.getPlayers()) {
            p.setHasActed(true);
        }
        player1.setHasActed(false);
        result.setCurrentPlayerIndex(0);
        
        result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        assertEquals(GameStatus.TURN, result.getGameStatus());
        assertEquals(4, result.getCommunityCards().size());
        
        // Reset for TURN -> RIVER
        for (Player p : result.getPlayers()) {
            p.setHasActed(true);
        }
        player1.setHasActed(false);
        result.setCurrentPlayerIndex(0);
        
        result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        assertEquals(GameStatus.RIVER, result.getGameStatus());
        assertEquals(5, result.getCommunityCards().size());
        
        // Reset for RIVER -> SHOWDOWN
        for (Player p : result.getPlayers()) {
            p.setHasActed(true);
        }
        player1.setHasActed(false);
        result.setCurrentPlayerIndex(0);
        
        result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        //assertEquals(GameStatus.SHOWDOWN, result.getGameStatus());
        //for now the SHOWDOWN to GAMEOVER transition goes internally, won't work
        // Verify game eventually reaches GAMEOVER
        assertEquals(GameStatus.GAMEOVER, result.getGameStatus());
    }

    @Test
    public void testOnlyOnePlayerRemaining() {
        // Setup game in PREFLOP state
        testGame.setGameStatus(GameStatus.PREFLOP);
        testGame.setCurrentPlayerIndex(0);
        
        // Two players fold
        player2.setHasFolded(true);
        player3.setHasFolded(true);
        
        // Set some pot amount
        testGame.setPot(100L);
        
        // Last player acts
        Game result = gameService.processPlayerAction(1L, 1L, PlayerAction.CHECK, 0L);
        
        // Verify game is over and pot awarded to last player
        assertEquals(GameStatus.GAMEOVER, result.getGameStatus());
        assertEquals(0L, result.getPot());
        assertEquals(1100L, player1.getCredit()); // 1000 initial + 100 pot
    }
}