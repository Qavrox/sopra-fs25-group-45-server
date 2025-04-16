package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameCreationPostDTO {

    private Long creatorId;
    private String password;
    private boolean isPublic;
    private int maximalPlayers;
    private int startCredit;
    private int smallBlind;
    private int bigBlind;


    
    public Long getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean getIsPublic() {
        return isPublic;
    }
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    public int getMaximalPlayers() {
        return maximalPlayers;
    }
    public void setMaximalPlayers(int maximalPlayers) {
        this.maximalPlayers = maximalPlayers;
    }
    public int getStartCredit() {
        return startCredit;
    }
    public void setStartCredit(int startCredit) {
        this.startCredit = startCredit;
    }

    public int getSmallBlind() {
        return smallBlind;
    }   

    public void setSmallBlind(int smallBlind) {
        this.smallBlind = smallBlind;
    }
    public int getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }
    
}
