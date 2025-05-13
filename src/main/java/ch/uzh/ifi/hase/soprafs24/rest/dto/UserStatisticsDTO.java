package ch.uzh.ifi.hase.soprafs24.rest.dto;

/**
 * UserStatisticsDTO
 * This class is used to transfer user statistics data to the frontend.
 */
public class UserStatisticsDTO {
    private Long userId;
    private String username;
    private String displayName;
    private Long gamesPlayed;
    private Long wins;
    private Long losses;
    private Double winRate;
    private Long totalWinnings;
    private Double averagePosition;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Long getWins() {
        return wins;
    }

    public void setWins(Long wins) {
        this.wins = wins;
    }

    public Long getLosses() {
        return losses;
    }

    public void setLosses(Long losses) {
        this.losses = losses;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Long getTotalWinnings() {
        return totalWinnings;
    }

    public void setTotalWinnings(Long totalWinnings) {
        this.totalWinnings = totalWinnings;
    }

    public Double getAveragePosition() {
        return averagePosition;
    }

    public void setAveragePosition(Double averagePosition) {
        this.averagePosition = averagePosition;
    }
}
