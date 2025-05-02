package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.Player;

public class GameResultsDTO {
    private Player winner;
    private String winningHand;
    private GameStatisticsDTO statistics;

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public String getWinningHand() {
        return winningHand;
    }

    public void setWinningHand(String winningHand) {
        this.winningHand = winningHand;
    }

    public GameStatisticsDTO getStatistics() {
        return statistics;
    }

    public void setStatistics(GameStatisticsDTO statistics) {
        this.statistics = statistics;
    }
} 