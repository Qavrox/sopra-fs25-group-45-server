package ch.uzh.ifi.hase.soprafs24.constant;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    @Test
    public void testInitialDeckHas52Cards() {
        Deck deck = new Deck();
        assertEquals(52, deck.remainingCards());
    }

    @Test
    public void testShuffleDeck() {
        // Create two decks with same order
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        
        // Check that both decks have the same initial order
        List<Card> cards1 = deck1.getCards();
        List<Card> cards2 = deck2.getCards();
        
        for (int i = 0; i < cards1.size(); i++) {
            assertEquals(cards1.get(i).getSuit(), cards2.get(i).getSuit());
            assertEquals(cards1.get(i).getRank(), cards2.get(i).getRank());
        }
        
        // Shuffle one deck
        deck1.shuffle();
        
        // Verify cards are same but likely in different order
        cards1 = deck1.getCards();
        cards2 = deck2.getCards();
        
        boolean allSamePosition = true;
        for (int i = 0; i < cards1.size(); i++) {
            if (!cards1.get(i).getSuit().equals(cards2.get(i).getSuit()) || 
                !cards1.get(i).getRank().equals(cards2.get(i).getRank())) {
                allSamePosition = false;
                break;
            }
        }
        
        // It's theoretically possible but extremely unlikely for a shuffle to result in the same order
        assertFalse(allSamePosition, "Deck was not shuffled");
    }

    @Test
    public void testToStringListAndBack() {
        Deck originalDeck = new Deck();
        List<String> stringList = originalDeck.toStringList();
        
        // Check list has 52 string representations
        assertEquals(52, stringList.size());
        
        // Convert back to deck
        Deck convertedDeck = Deck.fromStringList(stringList);
        
        // Verify both decks have same cards in same order
        List<Card> originalCards = originalDeck.getCards();
        List<Card> convertedCards = convertedDeck.getCards();
        
        assertEquals(originalCards.size(), convertedCards.size());
        
        for (int i = 0; i < originalCards.size(); i++) {
            assertEquals(originalCards.get(i).getSuit(), convertedCards.get(i).getSuit());
            assertEquals(originalCards.get(i).getRank(), convertedCards.get(i).getRank());
        }
    }

    @Test
    public void testDrawCard() {
        Deck deck = new Deck();
        int initialCount = deck.remainingCards();
        
        Card drawnCard = deck.drawCard();
        
        assertNotNull(drawnCard);
        assertEquals(initialCount - 1, deck.remainingCards());
    }

    @Test
    public void testAddCard() {
        Deck deck = new Deck();
        int initialCount = deck.remainingCards();
        
        Card newCard = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        deck.addCard(newCard);
        
        assertEquals(initialCount + 1, deck.remainingCards());
    }
} 