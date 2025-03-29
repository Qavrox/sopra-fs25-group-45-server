package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameCreationPostDTO {

    private int creatorId;
    private String password;
    private boolean isPublic;
    private int maximalPlayers;
    private int startCredit;


    
    public int getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(int creatorId) {
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
    
}
