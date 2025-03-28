package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import ch.uzh.ifi.hase.soprafs24.constant.GameType;


@Entity
@Table(name = "GAME")
public class Game implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private GameType gameType;

    @Column(nullable = false)
    private Long pot;

    @Column(nullable = false)
    private Long callAmount;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private int numberOfPlayers;

    @Column(nullable = false)
    private List<Player> players;
 
    @Column(nullable = false)    
    private List<Spectator> spectators;

    @Column(nullable = false)
    private List<Card> communityCards;
    
    @Column(nullable = false)    
    private int smallBlindIndex;

    @Column(nullable = false)
    private int bigBlindIndex;

    public String getPassword(){
        return this.password;
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

    List<Player> getPlayers(){
        return players;
    }

    List<Spectator> getSpectators(){
        return spectators;
    }

    public void addPlayer(Player player){
        this.players.add(player);
    }
    public void addSpectator(Spectator spectator){
        this.spectators.add(spectator);
    }    

    public void raisePot(Long amount){
        this.pot+=amount;
    }

    public void rotateBlinds(){
        this.smallBlindIndex=(this.smallBlindIndex + 1)%(this.numberOfPlayers);
        this.bigBlindIndex=(bigBlindIndex + 1)%(this.numberOfPlayers);

    }








    
}
