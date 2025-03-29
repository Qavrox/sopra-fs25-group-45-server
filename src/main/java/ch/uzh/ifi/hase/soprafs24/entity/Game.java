package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;



@Entity
@Table(name = "GAME")
public class Game implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    
    @Column(nullable = false)
    public int creatorId;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = true)
    private Long pot;

    @Column(nullable = true)
    private Long callAmount;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private int numberOfPlayers;

    @Column(nullable = true)
    private GameStatus gameStatus;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "GAME_SPECTATORS", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "spectator_id")    
    private List<Integer> spectators;

    @Column(nullable = false)
    @ElementCollection
    @CollectionTable(name = "GAME_COMMUNITY_CARDS", joinColumns = @JoinColumn(name = "game_id"))
    private List<String> cardDeck;


    @Column(nullable = false)
    @ElementCollection
    @CollectionTable(name = "GAME_COMMUNITY_CARDS", joinColumns = @JoinColumn(name = "game_id"))
    private List<String> communityCards;
    
    @Column(nullable = true)    
    private int smallBlindIndex;

    @Column(nullable = true)
    private int bigBlindIndex;

    @Column(nullable = false)
    private int maximalPlayers;    

    @Column(nullable = false)
    private Long startCredit;      

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id=id;
    }

    public Boolean getIsPublic(){
        return isPublic;
      }
    
      public void setIsPublic(Boolean isPublic){
        this.isPublic=isPublic;
      }

    public List<Player> getPlayers(){
        return players;
    }

    public List<Integer> getSpectators(){
        return spectators;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }
    public void addSpectator(Integer spectator){
        this.spectators.add(spectator);
    }    

    public void raisePot(Long amount){
        this.pot+=amount;
    }

    public void rotateBlinds(){
        this.smallBlindIndex=(this.smallBlindIndex + 1)%(this.numberOfPlayers);
        this.bigBlindIndex=(bigBlindIndex + 1)%(this.numberOfPlayers);

    }

    public void setStartBlinds(){
        this.smallBlindIndex=1;
        this.bigBlindIndex=0;
    }

    public void setStartCredit(Long startCredit){
        this.startCredit=startCredit;
    }
    public Long getStartCredit(){
        return startCredit;
    }

    public void setMaximalPlayers(int maximalPlayers){
        this.maximalPlayers=maximalPlayers;
    }
    public int getMaximalPlayers(){
        return maximalPlayers;
    }

    public void setCreatorId(int creatorId){
        this.creatorId=creatorId;
    }
    public int getCreatorId(){
        return creatorId;
    }

    public void setGameStatus(GameStatus gameStatus){
        this.gameStatus=gameStatus;
    }
    public GameStatus getGameStatus(){
        return gameStatus;
    }

    public void setCardDeck(List<String> cardDeck){
        this.cardDeck=cardDeck;
    }
    public List<String> getCardDeck(){
        return cardDeck;
    }
    public void setCommunityCards(List<String> communityCards){
        this.communityCards=communityCards;
    }
    public List<String> getCommunityCards(){
        return communityCards;
    }
    public void setCallAmount(Long callAmount){
        this.callAmount=callAmount;
    }
    public Long getCallAmount(){
        return callAmount;
    }
    public void setPot(Long pot){
        this.pot=pot;
    }
    public Long getPot(){
        return pot;
    }

    public String getRandomCard(){
        int randomIndex = (int) (Math.random() * cardDeck.size());
        String randomCard = cardDeck.get(randomIndex);
        cardDeck.remove(randomIndex);
        return randomCard;
    }








    
}
