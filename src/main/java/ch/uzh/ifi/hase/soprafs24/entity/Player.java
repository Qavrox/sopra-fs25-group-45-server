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


@Entity
@Table(name = "PLAYERS")
public class Player {

    public Player(Long userId, List<String> hand, Game game) {
        this.userId = userId;
        this.credit = game.getStartCredit();
        this.hand = hand;
        this.game = game;
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


}