package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ch.uzh.ifi.hase.soprafs24.constant.PlayerAction;

@Entity
@Table(name = "PLAYERS")
public class Player {

    public Player(Long userId, List<String> hand, Game game) {
        this.userId = userId;
        this.credit = game.getStartCredit();
        this.hand = hand;
        this.game = game;
        this.currentBet = 0L;
        this.hasFolded = false;
        this.hasActed = false;
    }

    protected Player() {
        // This empty constructor is needed by JPA (stupid ass shit)
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long credit;

    @ElementCollection
    @CollectionTable(name = "PLAYER_HANDS", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "card")
    private List<String> hand;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @Column(nullable = false)
    private Long currentBet;
    
    @Column(nullable = false)
    private boolean hasFolded;
    
    @Column(nullable = false)
    private boolean hasActed;
    
    @Column(nullable = true)
    private PlayerAction lastAction;

    public Long getUserId() {
        return userId;
    }

    public Long getCredit() {
        return credit;
    }
    
    public void setCredit(Long credit) {
        this.credit = credit;
    }
    
    public List<String> getHand() {
        return hand;
    }
    
    public void setHand(List<String> hand) {
        this.hand = hand;
    }
    
    public Long getCurrentBet() {
        return currentBet;
    }
    
    public void setCurrentBet(Long currentBet) {
        this.currentBet = currentBet;
    }
    
    public boolean getHasFolded() {
        return hasFolded;
    }
    
    public void setHasFolded(boolean hasFolded) {
        this.hasFolded = hasFolded;
    }
    
    public boolean getHasActed() {
        return hasActed;
    }
    
    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }
    
    public PlayerAction getLastAction() {
        return lastAction;
    }
    
    public void setLastAction(PlayerAction lastAction) {
        this.lastAction = lastAction;
    }
    
    public Long getId() {
        return id;
    }
}