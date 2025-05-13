package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GameHistoryDTO
 * This class is used to transfer game history data between the backend and frontend.
 */
public class GameHistoryDTO {
    private Long id;
    private Long userId;
    private Long gameId;
    private LocalDateTime playedAt;
    private String result;
    private Long winnings;
    private List<Long> otherPlayerIds;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getWinnings() {
        return winnings;
    }

    public void setWinnings(Long winnings) {
        this.winnings = winnings;
    }

    public List<Long> getOtherPlayerIds() {
        return otherPlayerIds;
    }

    public void setOtherPlayerIds(List<Long> otherPlayerIds) {
        this.otherPlayerIds = otherPlayerIds;
    }
}
