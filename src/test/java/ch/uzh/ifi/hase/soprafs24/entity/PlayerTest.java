package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private Game mockGame;

    @BeforeEach
    void setUp() {
        mockGame = new Game();
        mockGame.setStartCredit(1000L); // Set a start credit for the game

        List<String> hand = new ArrayList<>();
        hand.add("AH"); // Ace of Hearts
        hand.add("KH"); // King of Hearts

        // Initialize player using the constructor that takes a Game object
        player = new Player(1L, hand, mockGame);
        player.setId(100L); // Manually set player ID for testing
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        assertEquals(1L, player.getUserId());
        assertEquals(1000L, player.getCredit());
        assertNotNull(player.getHand());
        assertEquals(2, player.getHand().size());
        assertEquals("AH", player.getHand().get(0));
        assertEquals(0L, player.getCurrentBet());
        assertFalse(player.getHasFolded());
        assertFalse(player.getHasActed());
        assertNull(player.getLastAction());
        assertEquals(0L, player.getTotalBets());
    }

    @Test
    void testGetSetId() {
        player.setId(2L);
        assertEquals(2L, player.getId());
    }

    @Test
    void testGetSetCredit() {
        player.setCredit(500L);
        assertEquals(500L, player.getCredit());
    }

    @Test
    void testGetSetHand() {
        List<String> newHand = new ArrayList<>();
        newHand.add("QS"); // Queen of Spades
        player.setHand(newHand);
        assertEquals(newHand, player.getHand());
        assertEquals(1, player.getHand().size());
    }

    @Test
    void testGetSetCurrentBet() {
        player.setCurrentBet(100L);
        assertEquals(100L, player.getCurrentBet());
    }

    @Test
    void testGetSetHasFolded() {
        player.setHasFolded(true);
        assertTrue(player.getHasFolded());
    }

    @Test
    void testGetSetHasActed() {
        player.setHasActed(true);
        assertTrue(player.getHasActed());
    }

    @Test
    void testGetSetLastAction() {
        player.setLastAction(PlayerAction.RAISE);
        assertEquals(PlayerAction.RAISE, player.getLastAction());
    }

    @Test
    void testGetSetTotalBets() {
        player.setTotalBets(50L);
        assertEquals(50L, player.getTotalBets());
    }

    @Test
    void testAddToTotalBets() {
        player.setTotalBets(50L);
        player.addToTotalBets(25L);
        assertEquals(75L, player.getTotalBets());
    }

    @Test
    void testProtectedConstructor() {
        // This test is mainly for coverage and to ensure the protected constructor exists for JPA
        Player protectedPlayer = new Player();
        assertNotNull(protectedPlayer);
        // You might want to assert default values if they are set in the protected constructor,
        // but typically it's empty or initializes to null/default primitives.
        assertNull(protectedPlayer.getId());
        assertNull(protectedPlayer.getUserId());
        assertNull(protectedPlayer.getCredit());
    }
}