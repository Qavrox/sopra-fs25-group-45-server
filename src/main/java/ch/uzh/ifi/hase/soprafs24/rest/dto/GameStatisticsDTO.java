package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameStatisticsDTO {
    private double participationRate;
    private int potsWon;

    public double getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(double participationRate) {
        this.participationRate = participationRate;
    }

    public int getPotsWon() {
        return potsWon;
    }

    public void setPotsWon(int potsWon) {
        this.potsWon = potsWon;
    }
} 