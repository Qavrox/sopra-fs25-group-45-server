package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
//import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BettingRoundTest {

    private Game game;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        // Create a test game
        game = new Game();
        game.setCreatorId(1);
        game.setIsPublic(true);
        game.setMaximalPlayers(6);
        game.setStartCredit(1000L);
        game.setGameStatus(GameStatus.READY);
        game.setPot(0L);
        game.setCallAmount(0L);
        game.initializeShuffledDeck();
        game.setCommunityCards(new ArrayList<>());

        // Create test players
        List<String> hand1 = new ArrayList<>();
        hand1.add("AH");
        hand1.add("KH");
        player1 = new Player(1L, hand1, game);

        List<String> hand2 = new ArrayList<>();
        hand2.add("QS");
        hand2.add("JS");
        player2 = new Player(2L, hand2, game);

        List<String> hand3 = new ArrayList<>();
        hand3.add("10C");
        hand3.add("9C");
        player3 = new Player(3L, hand3, game);

        // Add players to game
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);

        // Set blinds
        game.setStartBlinds();
        
        // Set current player index
        game.setCurrentPlayerIndex(0);
    }

    @Test
    void testMoveToNextPlayer() {
        // Initial player is 0
        assertEquals(0, game.getCurrentPlayerIndex());
        
        // Move to next player
        game.moveToNextPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());
        
        // Move to next player again
        game.moveToNextPlayer();
        assertEquals(2, game.getCurrentPlayerIndex());
        
        // Move to next player, should wrap around to 0
        game.moveToNextPlayer();
        assertEquals(0, game.getCurrentPlayerIndex());
    }
    
    @Test
    void testMoveToNextPlayerSkipsFolded() {
        // Set player 1 as folded
        player2.setHasFolded(true);
        
        // Initial player is 0
        assertEquals(0, game.getCurrentPlayerIndex());
        
        // Move to next player, should skip player 1 and go to player 2
        game.moveToNextPlayer();
        assertEquals(2, game.getCurrentPlayerIndex());
    }
    
    @Test
    void testResetPlayerActions() {
        // Set all players as having acted
        player1.setHasActed(true);
        player2.setHasActed(true);
        player3.setHasActed(true);
        
        // Set current player to 0
        game.setCurrentPlayerIndex(0);
        
        // Reset player actions
        game.resetPlayerActions();
        
        // Verify all players' hasActed is reset to false
        assertFalse(player1.getHasActed());
        assertFalse(player2.getHasActed());
        assertFalse(player3.getHasActed());
        
        // Verify current player is set to after small blind
        assertEquals((game.getSmallBlindIndex() + 1) % game.getPlayers().size(), game.getCurrentPlayerIndex());
    }
    
    @Test
    void testIsBettingRoundComplete_AllActedEqualBets() {
        // Set all players as having acted with equal bets
        player1.setHasActed(true);
        player1.setCurrentBet(10L);
        
        player2.setHasActed(true);
        player2.setCurrentBet(10L);
        
        player3.setHasActed(true);
        player3.setCurrentBet(10L);
        
        // Betting round should be complete
        assertTrue(game.isBettingRoundComplete());
    }
    
    @Test
    void testIsBettingRoundComplete_NotAllActed() {
        // Set two players as having acted with equal bets
        player1.setHasActed(true);
        player1.setCurrentBet(10L);
        
        player2.setHasActed(true);
        player2.setCurrentBet(10L);
        
        // Third player hasn't acted
        player3.setHasActed(false);
        player3.setCurrentBet(0L);
        
        // Betting round should not be complete
        assertFalse(game.isBettingRoundComplete());
    }
    
    @Test
    void testIsBettingRoundComplete_UnequalBets() {
        // Set all players as having acted but with unequal bets
        player1.setHasActed(true);
        player1.setCurrentBet(10L);
        
        player2.setHasActed(true);
        player2.setCurrentBet(20L);
        
        player3.setHasActed(true);
        player3.setCurrentBet(10L);
        
        // Betting round should not be complete
        assertFalse(game.isBettingRoundComplete());
    }
    
    @Test
    void testIsBettingRoundComplete_OnlyOnePlayerRemaining() {
        // Set two players as folded
        player1.setHasFolded(true);
        player2.setHasFolded(true);
        
        // Only one player remains
        player3.setHasFolded(false);
        
        // Betting round should be complete
        assertTrue(game.isBettingRoundComplete());
    }
    
    @Test
    void testCollectBetsIntoPot() {
        // Set player bets
        player1.setCurrentBet(10L);
        player2.setCurrentBet(20L);
        player3.setCurrentBet(30L);
        
        // Initial pot
        game.setPot(50L);
        
        // Collect bets
        game.collectBetsIntoPot();
        
        // Verify pot increased by sum of bets
        assertEquals(110L, game.getPot());
        
        // Verify player bets reset to 0
        assertEquals(0L, player1.getCurrentBet());
        assertEquals(0L, player2.getCurrentBet());
        assertEquals(0L, player3.getCurrentBet());
    }
    
    @Test
    void testCollectBetsIntoPot_NullPot() {
        // Set player bets
        player1.setCurrentBet(10L);
        player2.setCurrentBet(20L);
        player3.setCurrentBet(30L);
        
        // Initial pot is null
        game.setPot(null);
        
        // Collect bets
        game.collectBetsIntoPot();
        
        // Verify pot is sum of bets
        assertEquals(60L, game.getPot());
        
        // Verify player bets reset to 0
        assertEquals(0L, player1.getCurrentBet());
        assertEquals(0L, player2.getCurrentBet());
        assertEquals(0L, player3.getCurrentBet());
    }
}