package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.service.GameService;

public class GameGetDTO {
    
  private Long id;
  private Long  creatorId;
  private String password;
  private Boolean isPublic;
  private int maximalPlayers;
  private int startCredit;
  private int smallBlind;
  private int bigBlind;
  private GameStatus gameStatus;
  private int pot;
  private int callAmount;
  private int smallBlindIndex;
  private int numberOfPlayers;
  private List<String> communityCards;
  private List<Player> players;
  private Long currentPlayerId;



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

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
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

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }
  public void setNumberOfPlayers(int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
  }

  // public List<Integer> getCommunityCards() { // 旧代码
  //   return communityCards;
  // }
  // public void setCommunityCards(List<Integer> communityCards) { // 旧代码
  //   this.communityCards = communityCards;
  // }
  public List<String> getCommunityCards() { // 修改后的代码
    return communityCards;
  }
  public void setCommunityCards(List<String> communityCards) { // 修改后的代码
    this.communityCards = communityCards;
  }
  public List<Player> getPlayers() {
    return players;
  }
  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public int getSmallBlind() {
    return smallBlind;
  }
  public void setSmallBlind(int smallBlind) {
    this.smallBlind = smallBlind;
  }
  public int getBigBlind() {
    return bigBlind;
  }
  public void setBigBlind(int bigBlind) {
    this.bigBlind = bigBlind;
  }
  
  public Long getCurrentPlayerId() {
    return currentPlayerId;
  }

  public void setCurrentPlayerId(Long currentPlayerId) {
    this.currentPlayerId = currentPlayerId;
  }
}
