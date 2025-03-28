package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PLAYERS")
public class Player {

    private User user;
    private Long credit;
    private List<Integer> hand;
    
    
    
}
