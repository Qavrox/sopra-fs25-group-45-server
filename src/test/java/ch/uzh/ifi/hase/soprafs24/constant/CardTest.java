package ch.uzh.ifi.hase.soprafs24.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void testToShortString() {
        Card aceOfSpades = new Card(Card.Suit.SPADES, Card.Rank.ACE);
        assertEquals("AS", aceOfSpades.toShortString());
        
        Card tenOfHearts = new Card(Card.Suit.HEARTS, Card.Rank.TEN);
        assertEquals("TH", tenOfHearts.toShortString());
        
        Card twoOfClubs = new Card(Card.Suit.CLUBS, Card.Rank.TWO);
        assertEquals("2C", twoOfClubs.toShortString());
        
        Card queenOfDiamonds = new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN);
        assertEquals("QD", queenOfDiamonds.toShortString());
    }
    
    @Test
    public void testFromShortString() {
        Card aceOfSpades = Card.fromShortString("AS");
        assertEquals(Card.Suit.SPADES, aceOfSpades.getSuit());
        assertEquals(Card.Rank.ACE, aceOfSpades.getRank());
        
        Card tenOfHearts = Card.fromShortString("TH");
        assertEquals(Card.Suit.HEARTS, tenOfHearts.getSuit());
        assertEquals(Card.Rank.TEN, tenOfHearts.getRank());
        
        Card twoOfClubs = Card.fromShortString("2C");
        assertEquals(Card.Suit.CLUBS, twoOfClubs.getSuit());
        assertEquals(Card.Rank.TWO, twoOfClubs.getRank());
        
        Card queenOfDiamonds = Card.fromShortString("QD");
        assertEquals(Card.Suit.DIAMONDS, queenOfDiamonds.getSuit());
        assertEquals(Card.Rank.QUEEN, queenOfDiamonds.getRank());
    }
    
    @Test
    public void testInvalidFromShortString() {
        assertThrows(IllegalArgumentException.class, () -> Card.fromShortString(""));
        assertThrows(IllegalArgumentException.class, () -> Card.fromShortString("X"));
        assertThrows(IllegalArgumentException.class, () -> Card.fromShortString("1Z"));
        assertThrows(IllegalArgumentException.class, () -> Card.fromShortString("AX"));
    }
    
    @Test
    public void testRoundTripConversion() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                Card original = new Card(suit, rank);
                String shortString = original.toShortString();
                Card converted = Card.fromShortString(shortString);
                
                assertEquals(original.getSuit(), converted.getSuit());
                assertEquals(original.getRank(), converted.getRank());
            }
        }
    }
} 