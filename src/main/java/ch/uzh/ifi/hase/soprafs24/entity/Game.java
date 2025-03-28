package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import ch.uzh.ifi.hase.soprafs24.constant.isPublic;


@Entity
@Table(name = "GAME")
public class Game implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "boolean default true")
    private Boolean isPublic;

    @Column(nullable = false)
    private Long pot;

    @Column(nullable = false)
    private Long callAmount;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private int numberOfPlayers;

    @OneToMany(mappedBy = "game")
    private List<Player> players;
 
    @OneToMany(mappedBy = "game")
    private List<Integer> spectators;

    @Column(nullable = false)
    private List<Integer> communityCards;
    
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


    public Boolean getIsPublic(){
        return isPublic;
      }
    
      public void setIsPublic(Boolean isPublic){
        this.isPublic=isPublic;
      }

    List<Player> getPlayers(){
        return players;
    }

    List<Integer> getSpectators(){
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








    
}
