package ch.uzh.ifi.hase.soprafs24.rest.dto;

/**
 * DTO for returning poker advice from the Gemini API
 */
public class PokerAdviceResponseDTO {
    private String advice;

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }
} 