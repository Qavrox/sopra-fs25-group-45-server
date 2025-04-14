package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;

public class PlayerActionPostDTO {
    private Long playerId;
    private PlayerAction action;
    private Long amount;
    
    public Long getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    
    public PlayerAction getAction() {
        return action;
    }
    
    public void setAction(PlayerAction action) {
        this.action = action;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
}