package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class GameCreationPostDTO {

    private Long creatorId;
    private String password;
    private boolean isPublic;
    private int maximalPlayers;
    private int startCredit;


    
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
    
}
