package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameType;

public class GameGetDTO {
    
  private Long id;
  private String password;
  private GameType gameType;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public GameType getGameType(){
    return gameType;
  }

  public void setGameType(GameType gameType){
    this.gameType=gameType;
  }
    
}
