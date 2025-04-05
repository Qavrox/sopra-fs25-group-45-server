package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.Deck;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setCreatorId(1);
        game.setIsPublic(true);
        game.setMaximalPlayers(6);
        game.setStartCredit(1000L);
        game.setGameStatus(GameStatus.WAITING);
    }

    @Test
    void testInitializeShuffledDeck() {
        // Initialize a shuffled deck
        game.initializeShuffledDeck();
        
        // Verify deck has been created
        assertNotNull(game.getCardDeck());
        
        // Standard deck should have 52 cards
        assertEquals(52, game.getCardDeck().size());
        
        // Get the deck as an object and verify
        Deck deckObject = game.getDeck();
        assertEquals(52, deckObject.remainingCards());
    }
    
    @Test
    void testDrawRandomCard() {
        // Initialize a shuffled deck
        game.initializeShuffledDeck();
        int initialDeckSize = game.getCardDeck().size();
        
        // Draw a card
        Card drawnCard = game.drawRandomCard();
        
        // Verify the card is not null
        assertNotNull(drawnCard);
        
        // Verify the deck size has decreased
        assertEquals(initialDeckSize - 1, game.getCardDeck().size());
        
        // Draw another card and make sure it's different
        Card secondCard = game.drawRandomCard();
        assertNotEquals(drawnCard.toShortString(), secondCard.toShortString());
    }
    
    @Test
    void testAddCommunityCard() {
        // Initialize community cards
        game.setCommunityCards(new ArrayList<>());
        
        // Create a card and add it
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        game.addCommunityCard(card);
        
        // Verify it was added
        assertEquals(1, game.getCommunityCards().size());
        assertEquals("AH", game.getCommunityCards().get(0));
        
        // Get community cards as objects
        List<Card> communityCards = game.getCommunityCardsAsObjects();
        assertEquals(1, communityCards.size());
        assertEquals(Card.Suit.HEARTS, communityCards.get(0).getSuit());
        assertEquals(Card.Rank.ACE, communityCards.get(0).getRank());
    }
    
    @Test
    void testGetRandomCard_BackwardCompatibility() {
        // Initialize a shuffled deck
        game.initializeShuffledDeck();
        int initialDeckSize = game.getCardDeck().size();
        
        // Use the old method
        String randomCard = game.getRandomCard();
        
        // Verify we got a valid card string
        assertNotNull(randomCard);
        assertTrue(randomCard.length() >= 2);
        
        // Verify the deck size decreased
        assertEquals(initialDeckSize - 1, game.getCardDeck().size());
    }
    
    @Test
    void testGetDeck_WithEmptyCardDeck() {
        // Set card deck to null
        game.setCardDeck(null);
        
        // Get deck should handle null and create an empty list
        Deck deck = game.getDeck();
        assertNotNull(deck);
        assertEquals(0, deck.remainingCards());
    }
    
    @Test
    void testSaveDeck() {
        // Create a new deck with only a few cards
        Deck deck = new Deck();
        deck.shuffle();
        
        // Remove some cards
        deck.drawCard();
        deck.drawCard();
        
        // Save the deck
        game.saveDeck(deck);
        
        // Verify the deck was saved with the correct number of cards
        assertEquals(50, game.getCardDeck().size());
    }
    
    @Test
    void testGetCommunityCardsAsObjects_WithEmptyCommunityCards() {
        // Set community cards to null
        game.setCommunityCards(null);
        
        // Should return empty list not null
        List<Card> cards = game.getCommunityCardsAsObjects();
        assertNotNull(cards);
        assertTrue(cards.isEmpty());
    }
    
    @Test
    void testDealingEntireHand() {
        // Initialize a shuffled deck
        game.initializeShuffledDeck();
        
        // Deal 5 community cards
        for (int i = 0; i < 5; i++) {
            Card card = game.drawRandomCard();
            game.addCommunityCard(card);
        }
        
        // Verify 5 community cards exist
        assertEquals(5, game.getCommunityCards().size());
        
        // Verify deck has 47 cards left (52 - 5)
        assertEquals(47, game.getCardDeck().size());
        
        // Get all community cards as objects
        List<Card> communityCards = game.getCommunityCardsAsObjects();
        assertEquals(5, communityCards.size());
        
        // Make sure all cards are unique
        List<String> cardStrings = new ArrayList<>();
        for (Card card : communityCards) {
            String cardString = card.toShortString();
            assertFalse(cardStrings.contains(cardString), "Duplicate card found: " + cardString);
            cardStrings.add(cardString);
        }
    }
    
    @Test
    void testCardDeckIntegrity() {
        // Initialize a shuffled deck
        game.initializeShuffledDeck();
        
        // Draw half the deck
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            drawnCards.add(game.drawRandomCard());
        }
        
        // Verify 26 cards left
        assertEquals(26, game.getCardDeck().size());
        
        // Make sure all drawn cards are unique
        List<String> cardStrings = new ArrayList<>();
        for (Card card : drawnCards) {
            String cardString = card.toShortString();
            assertFalse(cardStrings.contains(cardString), "Duplicate card found: " + cardString);
            cardStrings.add(cardString);
        }
    }
}
