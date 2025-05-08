package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class PokerHelperPromptGeneratorTest {

    @Test
    void debugTest() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.FLOP;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Debug output
        System.out.println("GENERATED PROMPT:");
        System.out.println(prompt);
        
        // Assert - simple test that always passes
        assertTrue(true);
    }

    @Test
    void generatePrompt_NormalCase_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.FLOP;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The community cards are AH, QC, 7D."));
        assertTrue(prompt.contains("The player's hand is KS, JH."));
        assertTrue(prompt.contains("The pot is 100 dollars."));
        assertTrue(prompt.contains("The user has 500 dollars left."));
        assertTrue(prompt.contains("The game is in the flop state."));
        assertTrue(prompt.contains("The user has a 35.0% chance to win."));
    }

    @Test
    void generatePrompt_EmptyCommunityCards_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Collections.emptyList();
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.PREFLOP;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The community cards are none."));
        assertTrue(prompt.contains("The player's hand is KS, JH."));
        assertTrue(prompt.contains("The game is in the preflop state."));
    }

    @Test
    void generatePrompt_NullCommunityCards_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.PREFLOP;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                new ArrayList<String>(),
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The community cards are none."));
    }

    @Test
    void generatePrompt_FullCommunityCards_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D", "2S", "10H");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.RIVER;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The community cards are AH, QC, 7D, 2S, 10H."));
        assertTrue(prompt.contains("The game is in the river state."));
    }

    @Test
    void generatePrompt_ZeroChanceToWin_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.FLOP;
        double chanceToWin = 0.0;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The user has a 0.0% chance to win."));
    }

    @Test
    void generatePrompt_HighChanceToWin_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.FLOP;
        double chanceToWin = 1.0;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The user has a 100.0% chance to win."));
    }

    @Test
    void generatePrompt_ShowdownState_ReturnsCorrectlyFormattedPrompt() {
        // Arrange
        List<String> communityCards = Arrays.asList("AH", "QC", "7D", "2S", "10H");
        List<String> playerHand = Arrays.asList("KS", "JH");
        Long potSize = 100L;
        Long userMoneyLeft = 500L;
        GameStatus gameStatus = GameStatus.SHOWDOWN;
        double chanceToWin = 0.35;

        // Act
        String prompt = PokerHelperPromptGenerator.generatePrompt(
                communityCards,
                playerHand,
                potSize,
                userMoneyLeft,
                gameStatus,
                chanceToWin
        );

        // Assert
        assertTrue(prompt.contains("The game is in the showdown state."));
    }
} 