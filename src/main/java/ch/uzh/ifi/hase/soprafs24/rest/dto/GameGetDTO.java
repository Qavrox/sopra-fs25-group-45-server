package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

public class GameGetDTO {
    
  private Long id;
  private int creatorId;
  private String password;
  private Boolean isPublic;
  private int maximalPlayers;
  private int startCredit;
  private GameStatus gameStatus;
  private int pot;
  private int callAmount;
  private int smallBlindIndex;
  private int bigBlindIndex;
  private int numberOfPlayers;
  private List<Integer> communityCards;
  private List<Integer> spectators;
  private List<Player> players;



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

  public Boolean getIsPublic(){
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic){
    this.isPublic=isPublic;
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
  public int getCreatorId() {
    return creatorId;
  }
  public void setCreatorId(int creatorId) {
    this.creatorId = creatorId;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }
  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }
  public int getPot() {
    return pot;
  }
  public void setPot(int pot) {
    this.pot = pot;
  }
  public int getCallAmount() {
    return callAmount;
  }
  public void setCallAmount(int callAmount) {
    this.callAmount = callAmount;
  }
  public int getSmallBlindIndex() {
    return smallBlindIndex;
  }
  public void setSmallBlindIndex(int smallBlindIndex) {
    this.smallBlindIndex = smallBlindIndex;
  }
  public int getBigBlindIndex() {
    return bigBlindIndex;
  }
  public void setBigBlindIndex(int bigBlindIndex) {
    this.bigBlindIndex = bigBlindIndex;
  }
  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }
  public void setNumberOfPlayers(int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
  }

  public List<Integer> getCommunityCards() {
    return communityCards;
  }
  public void setCommunityCards(List<Integer> communityCards) {
    this.communityCards = communityCards;
  }
  public List<Integer> getSpectators() {
    return spectators;
  }
  public void setSpectators(List<Integer> spectators) {
    this.spectators = spectators;
  }
  public List<Player> getPlayers() {
    return players;
  }
  public void setPlayers(List<Player> players) {
    this.players = players;
  }
    
}
