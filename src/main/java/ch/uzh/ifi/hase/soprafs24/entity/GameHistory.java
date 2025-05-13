package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GameHistory Entity
 * This class represents a game history record in the database.
 * Each record contains information about a user's participation in a game,
 * including the result and winnings.
 */
@Entity
@Table(name = "GAME_HISTORY")
public class GameHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long gameId;
    
    @Column(nullable = false)
    private LocalDateTime playedAt;
    
    @Column(nullable = false)
    private String result; // "Win" or "Loss"
    
    @Column(nullable = false)
    private Long winnings; // positive for win, negative for loss
    
    // Optional: store other players in the game
    @ElementCollection
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
