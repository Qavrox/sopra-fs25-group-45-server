package ch.uzh.ifi.hase.soprafs24.rest.dto;

/**
 * LeaderboardEntryDTO
 * This class represents an entry in the leaderboard.
 */
public class LeaderboardEntryDTO {
    private Long userId;
    private String username;
    private String displayName;
    private Double winRate;
    private Long totalWinnings;
    private Long gamesPlayed;
    private Long rank;

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

    public Long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }
}
