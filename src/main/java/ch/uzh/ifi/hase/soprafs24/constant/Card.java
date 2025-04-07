package ch.uzh.ifi.hase.soprafs24.constant;

import java.io.Serializable;

public class Card implements Serializable {
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }
    
    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14);
        
        private final int value;
        
        Rank(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    private final Suit suit;
    private final Rank rank;
    
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    @Override
    public String toString() {
        return rank.name() + " of " + suit.name();
    }
    
    // Returns a short representation like "AS" for Ace of Spades
    public String toShortString() {
        String rankSymbol;
        switch (rank) {
            case ACE: rankSymbol = "A"; break;
            case KING: rankSymbol = "K"; break;
            case QUEEN: rankSymbol = "Q"; break;
            case JACK: rankSymbol = "J"; break;
            case TEN: rankSymbol = "T"; break;
            default: rankSymbol = String.valueOf(rank.getValue());
        }
        
        String suitSymbol;
        switch (suit) {
            case HEARTS: suitSymbol = "H"; break;
            case DIAMONDS: suitSymbol = "D"; break;
            case CLUBS: suitSymbol = "C"; break;
            case SPADES: suitSymbol = "S"; break;
            default: suitSymbol = "";
        }
        
        return rankSymbol + suitSymbol;
    }
    
    // Parse a card from a short string representation
    public static Card fromShortString(String cardStr) {
        if (cardStr == null || cardStr.length() < 2) {
            throw new IllegalArgumentException("Invalid card string: " + cardStr);
        }
        
        char rankChar = cardStr.charAt(0);
        char suitChar = cardStr.charAt(cardStr.length() - 1);
        
        Rank rank;
        switch (rankChar) {
            case 'A': rank = Rank.ACE; break;
            case 'K': rank = Rank.KING; break;
            case 'Q': rank = Rank.QUEEN; break;
            case 'J': rank = Rank.JACK; break;
            case 'T': rank = Rank.TEN; break;
            case '1': // 10 is a special case with two chars
                if (cardStr.length() < 3 || cardStr.charAt(1) != '0') {
                    throw new IllegalArgumentException("Invalid rank: " + rankChar);
                }
                rank = Rank.TEN;
                break;
            default:
                int rankValue = Character.getNumericValue(rankChar);
                if (rankValue >= 2 && rankValue <= 9) {
                    rank = Rank.values()[rankValue - 2]; // Offset because enum starts with TWO
                } else {
                    throw new IllegalArgumentException("Invalid rank: " + rankChar);
                }
        }
        
        Suit suit;
        switch (suitChar) {
            case 'H': suit = Suit.HEARTS; break;
            case 'D': suit = Suit.DIAMONDS; break;
            case 'C': suit = Suit.CLUBS; break;
            case 'S': suit = Suit.SPADES; break;
            default: throw new IllegalArgumentException("Invalid suit: " + suitChar);
        }
        
        return new Card(suit, rank);
    }
    
    public char getSuitChar() {
        switch (suit) {
            case HEARTS: return 'H';
            case DIAMONDS: return 'D';
            case CLUBS: return 'C';
            case SPADES: return 'S';
            default: return ' ';
        }
    }
    
    public static int suitToIndex(char suit) {
        switch (suit) {
            case 'S': return 0;
            case 'H': return 1;
            case 'D': return 2;
            case 'C': return 3;
            default: return -1;
        }
    }
    
    public static char indexToSuit(int index) {
        switch (index) {
            case 0: return 'S';
            case 1: return 'H';
            case 2: return 'D';
            case 3: return 'C';
            default: return ' ';
        }
    }
}
