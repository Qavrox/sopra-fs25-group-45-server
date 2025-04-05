package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.Deck;

import java.util.*;

/**
 * Provides a reasonably simple approximation for the Odds of winning using Monte Carlo simulation
 */
public class OddsCalculator {
    // Represents the evaluated hand.
    // Categories: 8 = straight flush, 7 = four of a kind, 6 = full house, 
    // 5 = flush, 4 = straight, 3 = three of a kind, 2 = two pair, 
    // 1 = one pair, 0 = high card.
    public static class HandValue implements Comparable<HandValue> {
        public int category;
        public List<Integer> kickers; // Descending order

        public HandValue(int category, List<Integer> kickers) {
            this.category = category;
            this.kickers = kickers;
        }
        
        // Compare first by category then by kickers.
        public int compareTo(HandValue other) {
            if (this.category != other.category)
                return this.category - other.category;
            int len = Math.min(this.kickers.size(), other.kickers.size());
            for (int i = 0; i < len; i++) {
                int cmp = this.kickers.get(i) - other.kickers.get(i);
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    }
    
    // Evaluates a 7-card hand (player's 2 cards + 5 board cards).
    // Uses a simple evaluation method to detect straights, flushes, pairs, etc.
    public static HandValue evaluateHand(List<Card> cards) {
        int[] rankCount = new int[15]; // indices 2-14 used.
        int[] suitCount = new int[4];   // 0:S, 1:H, 2:D, 3:C
        
        for (Card c : cards) {
            rankCount[c.getRank().getValue()]++;
            suitCount[Card.suitToIndex(c.getSuitChar())]++;
        }
        
        // Check for flush.
        int flushSuitIndex = -1;
        for (int i = 0; i < 4; i++) {
            if (suitCount[i] >= 5) {
                flushSuitIndex = i;
                break;
            }
        }
        boolean isFlush = flushSuitIndex != -1;
        
        // Check for straight.
        int straightHigh = 0, consecutive = 0, candidateHigh = 0;
        for (int r = 14; r >= 2; r--) {
            if (rankCount[r] > 0) {
                if (consecutive == 0) candidateHigh = r;
                consecutive++;
                if (consecutive >= 5) {
                    straightHigh = candidateHigh;
                    break;
                }
            } else {
                consecutive = 0;
            }
        }
        // Special case: A-2-3-4-5 straight.
        if (rankCount[14] > 0 && rankCount[5] > 0 && rankCount[4] > 0 &&
            rankCount[3] > 0 && rankCount[2] > 0) {
            straightHigh = Math.max(straightHigh, 5);
        }
        
        // Check for straight flush.
        int straightFlushHigh = 0;
        if (isFlush) {
            char flushSuit = Card.indexToSuit(flushSuitIndex);
            int[] flushRankCount = new int[15];
            for (Card c : cards) {
                if (c.getSuitChar() == flushSuit)
                    flushRankCount[c.getRank().getValue()]++;
            }
            consecutive = 0;
            candidateHigh = 0;
            for (int r = 14; r >= 2; r--) {
                if (flushRankCount[r] > 0) {
                    if (consecutive == 0) candidateHigh = r;
                    consecutive++;
                    if (consecutive >= 5) {
                        straightFlushHigh = candidateHigh;
                        break;
                    }
                } else {
                    consecutive = 0;
                }
            }
            if (flushRankCount[14] > 0 && flushRankCount[5] > 0 && flushRankCount[4] > 0 &&
                flushRankCount[3] > 0 && flushRankCount[2] > 0) {
                straightFlushHigh = Math.max(straightFlushHigh, 5);
            }
        }
        
        if (straightFlushHigh > 0)
            return new HandValue(8, Arrays.asList(straightFlushHigh));
        
        // Four of a kind.
        for (int r = 14; r >= 2; r--) {
            if (rankCount[r] == 4) {
                int kicker = 0;
                for (int k = 14; k >= 2; k--) {
                    if (k != r && rankCount[k] > 0) {
                        kicker = k;
                        break;
                    }
                }
                return new HandValue(7, Arrays.asList(r, kicker));
            }
        }
        
        // Full house.
        int threeRank = -1, pairRank = -1;
        for (int r = 14; r >= 2; r--) {
            if (rankCount[r] >= 3 && threeRank == -1)
                threeRank = r;
            else if (rankCount[r] >= 2 && pairRank == -1)
                pairRank = r;
        }
        if (threeRank != -1 && pairRank != -1)
            return new HandValue(6, Arrays.asList(threeRank, pairRank));
        
        // Flush.
        if (isFlush) {
            char flushSuit = Card.indexToSuit(flushSuitIndex);
            List<Integer> flushCards = new ArrayList<>();
            for (Card c : cards) {
                if (c.getSuitChar() == flushSuit)
                    flushCards.add(c.getRank().getValue());
            }
            flushCards.sort(Collections.reverseOrder());
            List<Integer> kickers = flushCards.subList(0, Math.min(5, flushCards.size()));
            return new HandValue(5, new ArrayList<>(kickers));
        }
        
        // Straight.
        if (straightHigh > 0)
            return new HandValue(4, Arrays.asList(straightHigh));
        
        // Three of a kind.
        for (int r = 14; r >= 2; r--) {
            if (rankCount[r] == 3) {
                List<Integer> kickers = new ArrayList<>();
                for (int k = 14; k >= 2; k--) {
                    if (k != r && rankCount[k] > 0) {
                        for (int i = 0; i < rankCount[k]; i++)
                            kickers.add(k);
                    }
                }
                while (kickers.size() > 2)
                    kickers.remove(kickers.size() - 1);
                List<Integer> vals = new ArrayList<>();
                vals.add(r);
                vals.addAll(kickers);
                return new HandValue(3, vals);
            }
        }
        
        // Two pair.
        int highPair = -1, lowPair = -1;
        for (int r = 14; r >= 2; r--) {
            if (rankCount[r] == 2) {
                if (highPair == -1)
                    highPair = r;
                else if (lowPair == -1) {
                    lowPair = r;
                    break;
                }
            }
        }
        if (highPair != -1 && lowPair != -1) {
            int kicker = 0;
            for (int r = 14; r >= 2; r--) {
                if (r != highPair && r != lowPair && rankCount[r] > 0) {
                    kicker = r;
                    break;
                }
            }
            return new HandValue(2, Arrays.asList(highPair, lowPair, kicker));
        }
        
        // One pair.
        if (highPair != -1) {
            List<Integer> kickers = new ArrayList<>();
            for (int r = 14; r >= 2; r--) {
                if (r != highPair && rankCount[r] > 0) {
                    for (int i = 0; i < rankCount[r]; i++)
                        kickers.add(r);
                }
            }
            while (kickers.size() > 3)
                kickers.remove(kickers.size() - 1);
            List<Integer> vals = new ArrayList<>();
            vals.add(highPair);
            vals.addAll(kickers);
            return new HandValue(1, vals);
        }
        
        // High card.
        List<Integer> highCards = new ArrayList<>();
        for (int r = 14; r >= 2; r--) {
            for (int i = 0; i < rankCount[r]; i++) {
                highCards.add(r);
            }
        }
        while (highCards.size() > 5)
            highCards.remove(highCards.size() - 1);
        return new HandValue(0, highCards);
    }
    
    /**
     * Monte Carlo simulation to estimate winning odds.
     * Instead of enumerating every possible outcome, we sample random deals.
     *
     * @param handStr      Two-card array for the player (e.g. {"AS", "KD"}).
     * @param boardStr     0–5 board cards.
     * @param numOpponents Number of opponents.
     * @param iterations   Number of simulation iterations.
     * @return Approximate winning probability.
     */
    public static double calculateOdds(String[] handStr, String[] boardStr, int numOpponents, int iterations) {
        List<Card> playerHand = new ArrayList<>();
        for (String s : handStr) {
            playerHand.add(Card.fromShortString(s));
        }
        List<Card> board = new ArrayList<>();
        for (String s : boardStr) {
            board.add(Card.fromShortString(s));
        }
        
        // Build deck excluding known cards.
        Set<Card> known = new HashSet<>();
        known.addAll(playerHand);
        known.addAll(board);
        List<Card> deck = Deck.createDeckExcluding(known);
        
        int missingBoard = 5 - board.size();
        Random rng = new Random();
        double totalWeight = 0.0;
        
        for (int iter = 0; iter < iterations; iter++) {
            // Copy deck into an array for shuffling.
            Card[] simDeck = deck.toArray(new Card[deck.size()]);
            // Fisher-Yates shuffle.
            for (int j = simDeck.length - 1; j > 0; j--) {
                int k = rng.nextInt(j + 1);
                Card temp = simDeck[j];
                simDeck[j] = simDeck[k];
                simDeck[k] = temp;
            }
            
            // Complete the board.
            List<Card> fullBoard = new ArrayList<>(board);
            for (int j = 0; j < missingBoard; j++) {
                fullBoard.add(simDeck[j]);
            }
            
            // Evaluate player's 7-card hand.
            List<Card> playerSeven = new ArrayList<>(playerHand);
            playerSeven.addAll(fullBoard);
            HandValue playerValue = evaluateHand(playerSeven);
            
            // Deal opponent hands sequentially.
            HandValue bestOpp = null;
            int tiedOpponents = 0;
            int offset = missingBoard; // opponents' cards start right after the board completions.
            for (int opp = 0; opp < numOpponents; opp++) {
                List<Card> oppHand = new ArrayList<>(2);
                oppHand.add(simDeck[offset + opp * 2]);
                oppHand.add(simDeck[offset + opp * 2 + 1]);
                List<Card> oppSeven = new ArrayList<>(oppHand);
                oppSeven.addAll(fullBoard);
                HandValue oppValue = evaluateHand(oppSeven);
                if (bestOpp == null || oppValue.compareTo(bestOpp) > 0) {
                    bestOpp = oppValue;
                    tiedOpponents = 1;
                } else if (oppValue.compareTo(bestOpp) == 0) {
                    tiedOpponents++;
                }
            }
            
            int cmp = playerValue.compareTo(bestOpp);
            double scenarioWeight = cmp > 0 ? 1.0 : (cmp == 0 ? 1.0 / (tiedOpponents + 1) : 0.0);
            totalWeight += scenarioWeight;
        }
        
        return totalWeight / iterations;
    }
}
