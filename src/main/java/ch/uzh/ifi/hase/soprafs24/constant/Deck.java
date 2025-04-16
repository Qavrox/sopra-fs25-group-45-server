package ch.uzh.ifi.hase.soprafs24.constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Deck implements Serializable {
    private List<Card> cards;
    
    /**
     * Creates a new deck with all 52 cards in order.
     */
    public Deck() {
        this.cards = new ArrayList<>();
        initializeStandardDeck();
    }
    
    /**
     * Initializes a standard 52-card deck.
     */
    private void initializeStandardDeck() {
        this.cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }
    
    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    /**
     * Draws a card from the top of the deck.
     * @return the drawn card
     * @throws IllegalStateException if the deck is empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot draw from an empty deck");
        }
        return cards.remove(0);
    }
    
    /**
     * Returns the number of cards remaining in the deck.
     * @return the number of cards
     */
    public int remainingCards() {
        return cards.size();
    }
    
    /**
     * Checks if the deck is empty.
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /**
     * Resets the deck to a full 52-card deck and shuffles it.
     */
    public void resetAndShuffle() {
        initializeStandardDeck();
        shuffle();
    }
    
    /**
     * Converts the deck to a list of string representations.
     * Useful for persistence.
     * @return list of card strings
     */
    public List<String> toStringList() {
        return cards.stream()
                .map(Card::toShortString)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a deck from a list of card string representations.
     * @param cardStrings list of card strings
     * @return a new deck with the specified cards
     */
    public static Deck fromStringList(List<String> cardStrings) {
        Deck deck = new Deck();
        deck.cards.clear();
        
        for (String cardStr : cardStrings) {
            deck.cards.add(Card.fromShortString(cardStr));
        }
        
        return deck;
    }
    
    /**
     * Returns all cards currently in the deck.
     * @return an unmodifiable view of the cards in the deck
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
    
    /**
     * Add a card to the bottom of the deck.
     * @param card the card to add
     */
    public void addCard(Card card) {
        this.cards.add(card);
    }
    
    /**
     * Add a card to a specific position in the deck.
     * @param index the position to add the card
     * @param card the card to add
     */
    public void addCard(int index, Card card) {
        this.cards.add(index, card);
    }
    
    @Override
    public String toString() {
        return "Deck with " + cards.size() + " cards";
    }
    
    /**
     * Creates a deck excluding the specified cards.
     * @param knownCards set of cards to exclude from the deck
     * @return a list of cards excluding the known cards
     */
    public static List<Card> createDeckExcluding(Set<Card> knownCards) {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                Card card = new Card(suit, rank);
                if (!knownCards.contains(card)) {
                    deck.add(card);
                }
            }
        }
        return deck;
    }
}
