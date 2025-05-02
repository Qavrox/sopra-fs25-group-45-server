package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;

public class PlayerActionPostDTO {
    private Long userId;
    private PlayerAction action;
    private Long amount;
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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