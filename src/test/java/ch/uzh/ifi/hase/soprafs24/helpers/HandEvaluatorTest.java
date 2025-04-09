package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HandEvaluatorTest {

    @Test
    public void testStraightFlushVsFourOfAKind() {
        // Create a straight flush hand
        List<Card> straightFlushHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KH"),
            Card.fromShortString("QH"),
            Card.fromShortString("JH"),
            Card.fromShortString("TH"),
            Card.fromShortString("2S"),
            Card.fromShortString("3C")
        );

        // Create a four of a kind hand
        List<Card> fourOfAKindHand = Arrays.asList(
            Card.fromShortString("AS"),
            Card.fromShortString("AD"),
            Card.fromShortString("AC"),
            Card.fromShortString("AH"),
            Card.fromShortString("KD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3D")
        );

        OddsCalculator.HandValue straightFlushValue = OddsCalculator.evaluateHand(straightFlushHand);
        OddsCalculator.HandValue fourOfAKindValue = OddsCalculator.evaluateHand(fourOfAKindHand);

        assertTrue(straightFlushValue.compareTo(fourOfAKindValue) > 0, 
                "Straight flush should be higher than four of a kind");
        assertEquals(8, straightFlushValue.category, "Straight flush should have category 8");
        assertEquals(7, fourOfAKindValue.category, "Four of a kind should have category 7");
    }

    @Test
    public void testFullHouseVsFlush() {
        // Create a full house hand
        List<Card> fullHouseHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("AD"),
            Card.fromShortString("KH"),
            Card.fromShortString("KD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        // Create a flush hand
        List<Card> flushHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KH"),
            Card.fromShortString("JH"),
            Card.fromShortString("9H"),
            Card.fromShortString("5H"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue fullHouseValue = OddsCalculator.evaluateHand(fullHouseHand);
        OddsCalculator.HandValue flushValue = OddsCalculator.evaluateHand(flushHand);

        assertTrue(fullHouseValue.compareTo(flushValue) > 0, 
                "Full house should be higher than flush");
        assertEquals(6, fullHouseValue.category, "Full house should have category 6");
        assertEquals(5, flushValue.category, "Flush should have category 5");
    }

    @Test
    public void testStraightVsThreeOfAKind() {
        // Create a straight hand
        List<Card> straightHand = Arrays.asList(
            Card.fromShortString("9H"),
            Card.fromShortString("8S"),
            Card.fromShortString("7D"),
            Card.fromShortString("6C"),
            Card.fromShortString("5H"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        // Create a three of a kind hand
        List<Card> threeOfAKindHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("AD"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue straightValue = OddsCalculator.evaluateHand(straightHand);
        OddsCalculator.HandValue threeOfAKindValue = OddsCalculator.evaluateHand(threeOfAKindHand);

        assertTrue(straightValue.compareTo(threeOfAKindValue) > 0, 
                "Straight should be higher than three of a kind");
        assertEquals(4, straightValue.category, "Straight should have category 4");
        assertEquals(3, threeOfAKindValue.category, "Three of a kind should have category 3");
    }

    @Test
    public void testTwoPairVsOnePair() {
        // Create a two pair hand
        List<Card> twoPairHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("KD"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        // Create a one pair hand
        List<Card> onePairHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("JC"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue twoPairValue = OddsCalculator.evaluateHand(twoPairHand);
        OddsCalculator.HandValue onePairValue = OddsCalculator.evaluateHand(onePairHand);

        assertTrue(twoPairValue.compareTo(onePairValue) > 0, 
                "Two pair should be higher than one pair");
        assertEquals(2, twoPairValue.category, "Two pair should have category 2");
        assertEquals(1, onePairValue.category, "One pair should have category 1");
    }

    @Test
    public void testOnePairVsHighCard() {
        // Create a one pair hand
        List<Card> onePairHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("JC"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        // Create a high card hand
        List<Card> highCardHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KS"),
            Card.fromShortString("QH"),
            Card.fromShortString("JD"),
            Card.fromShortString("9C"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue onePairValue = OddsCalculator.evaluateHand(onePairHand);
        OddsCalculator.HandValue highCardValue = OddsCalculator.evaluateHand(highCardHand);

        assertTrue(onePairValue.compareTo(highCardValue) > 0, 
                "One pair should be higher than high card");
        assertEquals(1, onePairValue.category, "One pair should have category 1");
        assertEquals(0, highCardValue.category, "High card should have category 0");
    }

    @Test
    public void testCompareHighCardsWithSameCategory() {
        // Create two high card hands with different high cards
        // The second hand was inadvertently making a K-Q-J-10-9 straight, so change one card
        List<Card> highCardHand1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KS"),
            Card.fromShortString("QH"),
            Card.fromShortString("JD"),
            Card.fromShortString("9C"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> highCardHand2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("QS"),
            Card.fromShortString("JH"),
            Card.fromShortString("7D"),  // Changed from 10 to 7 to avoid making a straight
            Card.fromShortString("5C"),  // Changed from 9C to further avoid a straight
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue highCardValue1 = OddsCalculator.evaluateHand(highCardHand1);
        OddsCalculator.HandValue highCardValue2 = OddsCalculator.evaluateHand(highCardHand2);

        // Print the exact values for debugging
        System.out.println("Hand 1 category: " + highCardValue1.category + ", kickers: " + highCardValue1.kickers);
        System.out.println("Hand 2 category: " + highCardValue2.category + ", kickers: " + highCardValue2.kickers);
        
        int comparison = highCardValue1.compareTo(highCardValue2);
        System.out.println("Comparison result: " + comparison);
        
        assertTrue(comparison > 0, 
                "Ace high should be higher than King high");
        assertEquals(0, highCardValue1.category, "First hand should be high card (category 0)");
        assertEquals(0, highCardValue2.category, "Second hand should be high card (category 0)");
    }

    @Test
    public void testWheelStraight() {
        // A-2-3-4-5 straight (the "wheel")
        List<Card> wheelHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("2S"),
            Card.fromShortString("3H"),
            Card.fromShortString("4D"),
            Card.fromShortString("5C"),
            Card.fromShortString("KC"),
            Card.fromShortString("QS")
        );

        OddsCalculator.HandValue wheelValue = OddsCalculator.evaluateHand(wheelHand);
        
        assertEquals(4, wheelValue.category, "Wheel should be a straight (category 4)");
        assertEquals(5, wheelValue.kickers.get(0).intValue(), 
                "The high card of a wheel straight should be 5");
    }

    @Test
    public void testStraightFlushWheel() {
        // A-2-3-4-5 straight flush (the "steel wheel")
        List<Card> steelWheelHand = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("2H"),
            Card.fromShortString("3H"),
            Card.fromShortString("4H"),
            Card.fromShortString("5H"),
            Card.fromShortString("KC"),
            Card.fromShortString("QS")
        );

        OddsCalculator.HandValue steelWheelValue = OddsCalculator.evaluateHand(steelWheelHand);
        
        assertEquals(8, steelWheelValue.category, "Steel wheel should be a straight flush (category 8)");
        assertEquals(5, steelWheelValue.kickers.get(0).intValue(), 
                "The high card of a steel wheel should be 5");
    }

    @Test
    public void testTiedStraightFlushes() {
        // Two straight flushes with different high cards
        List<Card> straightFlush1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KH"),
            Card.fromShortString("QH"),
            Card.fromShortString("JH"),
            Card.fromShortString("TH"),
            Card.fromShortString("2S"),
            Card.fromShortString("3C")
        );

        List<Card> straightFlush2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("QH"),
            Card.fromShortString("JH"),
            Card.fromShortString("TH"),
            Card.fromShortString("9H"),
            Card.fromShortString("2S"),
            Card.fromShortString("3C")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(straightFlush1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(straightFlush2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Ace-high straight flush should be higher than King-high straight flush");
        assertEquals(8, value1.category, "Both hands should be straight flushes (category 8)");
        assertEquals(8, value2.category, "Both hands should be straight flushes (category 8)");
    }

    @Test
    public void testTiedFourOfAKinds() {
        // Two four of a kinds with different quads
        List<Card> fourOfAKind1 = Arrays.asList(
            Card.fromShortString("AS"),
            Card.fromShortString("AD"),
            Card.fromShortString("AC"),
            Card.fromShortString("AH"),
            Card.fromShortString("KD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3D")
        );

        List<Card> fourOfAKind2 = Arrays.asList(
            Card.fromShortString("KS"),
            Card.fromShortString("KD"),
            Card.fromShortString("KC"),
            Card.fromShortString("KH"),
            Card.fromShortString("AD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3D")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(fourOfAKind1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(fourOfAKind2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Four Aces should be higher than four Kings");
        assertEquals(7, value1.category, "Both hands should be four of a kind (category 7)");
        assertEquals(7, value2.category, "Both hands should be four of a kind (category 7)");
    }

    @Test
    public void testTiedFullHouses() {
        // Two full houses with different trips
        List<Card> fullHouse1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("AD"),
            Card.fromShortString("KH"),
            Card.fromShortString("KD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> fullHouse2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("KS"),
            Card.fromShortString("KD"),
            Card.fromShortString("AH"),
            Card.fromShortString("AD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(fullHouse1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(fullHouse2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Aces full of Kings should be higher than Kings full of Aces");
        assertEquals(6, value1.category, "Both hands should be full houses (category 6)");
        assertEquals(6, value2.category, "Both hands should be full houses (category 6)");
    }

    @Test
    public void testTiedFlushes() {
        // Two flushes with different high cards
        List<Card> flush1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KH"),
            Card.fromShortString("JH"),
            Card.fromShortString("9H"),
            Card.fromShortString("5H"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> flush2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("QH"),
            Card.fromShortString("JH"),
            Card.fromShortString("9H"),
            Card.fromShortString("5H"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(flush1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(flush2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Ace-high flush should be higher than King-high flush");
        assertEquals(5, value1.category, "Both hands should be flushes (category 5)");
        assertEquals(5, value2.category, "Both hands should be flushes (category 5)");
    }

    @Test
    public void testTiedTwoPairs() {
        // Two two-pair hands with different high pairs
        List<Card> twoPair1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("KD"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> twoPair2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("KS"),
            Card.fromShortString("QD"),
            Card.fromShortString("QH"),
            Card.fromShortString("AD"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(twoPair1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(twoPair2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Aces and Kings should be higher than Kings and Queens");
        assertEquals(2, value1.category, "Both hands should be two pairs (category 2)");
        assertEquals(2, value2.category, "Both hands should be two pairs (category 2)");
    }

    @Test
    public void testTiedOnePairs() {
        // Two one-pair hands with different pairs
        List<Card> onePair1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("AS"),
            Card.fromShortString("KH"),
            Card.fromShortString("QD"),
            Card.fromShortString("JC"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> onePair2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("KS"),
            Card.fromShortString("AH"),
            Card.fromShortString("QD"),
            Card.fromShortString("JC"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(onePair1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(onePair2);

        assertTrue(value1.compareTo(value2) > 0, 
                "Pair of Aces should be higher than pair of Kings");
        assertEquals(1, value1.category, "Both hands should be one pair (category 1)");
        assertEquals(1, value2.category, "Both hands should be one pair (category 1)");
    }

    @Test
    public void testTiedHighCards() {
        // Two high card hands with different high cards
        List<Card> highCard1 = Arrays.asList(
            Card.fromShortString("AH"),
            Card.fromShortString("KS"),
            Card.fromShortString("QH"),
            Card.fromShortString("JD"),
            Card.fromShortString("9C"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        List<Card> highCard2 = Arrays.asList(
            Card.fromShortString("KH"),
            Card.fromShortString("QS"),
            Card.fromShortString("JH"),
            Card.fromShortString("TD"),
            Card.fromShortString("4C"),
            Card.fromShortString("2C"),
            Card.fromShortString("3S")
        );

        OddsCalculator.HandValue value1 = OddsCalculator.evaluateHand(highCard1);
        OddsCalculator.HandValue value2 = OddsCalculator.evaluateHand(highCard2);

        assertEquals(0, value1.category, "Both hands should be high card (category 0), but hand 1 is not");
        assertEquals(0, value2.category, "Both hands should be high card (category 0), but hand 2 is not");
        assertTrue(value1.compareTo(value2) > 0, 
                "Ace-high should be higher than King-high");
    }
} 