package ch.uzh.ifi.hase.soprafs24.entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.Deck;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;



@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    
    @Column(nullable = false)
    public Long creatorId;

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

    @Column(nullable = false)
    private int smallBlind;

    @Column(nullable = false)
    private int bigBlind;

    @Column(nullable = false)
    private int smallBlindIndex;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "GAME_WINNERS",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> winners = new ArrayList<>();

    @Column(name = "card", nullable = false)
    @ElementCollection
    @CollectionTable(name = "GAME_CARD_DECK", joinColumns = @JoinColumn(name = "game_id"))
    private List<String> cardDeck;

    @Column(name = "card", nullable = false)
    @ElementCollection
    @CollectionTable(name = "GAME_COMMUNITY_CARDS", joinColumns = @JoinColumn(name = "game_id"))
    private List<String> communityCards;


    @Column(nullable = false)
    private int maximalPlayers;  
    
    @Column(nullable = true)
    private Long currentPlayerId;

    @Column(nullable = false)
    private Long startCredit; 

    @Column(nullable = true)
    private int currentPlayerIndex;
    
    @Column(nullable = true)
    private int lastRaisePlayerIndex;   

    @Column(nullable = true)
    private long userTurnId;

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

    public void setPlayers(List<Player> players){
        this.players=players;
    }

    public void addPlayer(Player player){
        this.players.add(player);
        this.numberOfPlayers = this.players.size();
    } 

    public void raisePot(Long amount){
        this.pot+=amount;
    }

    public void rotateBlinds(){
        this.smallBlindIndex=(this.smallBlindIndex + 1)%(this.numberOfPlayers);
    }

    public void setStartBlinds(){
        this.smallBlindIndex=0;
    }

    public int getSmallBlindIndex(){
        return smallBlindIndex;
    }

    public int getBigBlindIndex() {
        return (smallBlindIndex + 1) % numberOfPlayers;
    }

    public void setSmallBlindIndex(int smallBlindIndex){
        this.smallBlindIndex=smallBlindIndex;
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

    public void setCreatorId(Long creatorId){
        this.creatorId=creatorId;
    }
    public Long getCreatorId(){
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

    public List<Player> getWinners() {
        return winners;
    }
    
    public void setWinners(List<Player> winners) {
        this.winners = winners;
    }

    /**
     * Get a Deck object from the stored cardDeck strings
     */
    public Deck getDeck() {
        if (cardDeck == null) {
            cardDeck = new ArrayList<>();
        }
        return Deck.fromStringList(cardDeck);
    }
    
    /**
     * Save a Deck object to the cardDeck strings
     */
    public void saveDeck(Deck deck) {
        this.cardDeck = deck.toStringList();
    }
    
    /**
     * Get community cards as Card objects
     */
    public List<Card> getCommunityCardsAsObjects() {
        List<Card> cards = new ArrayList<>();
        if (communityCards != null) {
            for (String cardStr : communityCards) {
                cards.add(Card.fromShortString(cardStr));
            }
        }
        return cards;
    }
    
    /**
     * Add a Card to community cards
     */
    public void addCommunityCard(Card card) {
        if (communityCards == null) {
            communityCards = new ArrayList<>();
        }
        communityCards.add(card.toShortString());
    }
    
    /**
     * Draw a random card from the deck
     */
    public Card drawRandomCard() {
        Deck deck = getDeck();
        Card card = deck.drawCard();
        saveDeck(deck);
        return card;
    }

    /**
     * Initialize a new shuffled deck
     */
    public void initializeShuffledDeck() {
        Deck deck = new Deck();
        deck.shuffle();
        this.cardDeck = deck.toStringList();
    }
    
    /**
     * For backward compatibility
     */
    public String getRandomCard() {
        Card card = drawRandomCard();
        return card.toShortString();
    }   

    public void removePlayer(Player player) {
        this.players.remove(player);
        this.numberOfPlayers = this.players.size();
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
    // Add these fields to the Game class
    
    
    // Add these methods to the Game class
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
    
    public int getLastRaisePlayerIndex() {
        return lastRaisePlayerIndex;
    }
    
    public void setLastRaisePlayerIndex(int lastRaisePlayerIndex) {
        this.lastRaisePlayerIndex = lastRaisePlayerIndex;
    }
    
    /**
     * Move to the next active player
     */
    public void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            setCurrentPlayerId(players.get(currentPlayerIndex).getUserId());
        } while (players.get(currentPlayerIndex).getHasFolded());
    }
    
    /**
     * Reset player actions for a new betting round
     */
    public void resetPlayerActions() {
        for (Player player : players) {
            player.setHasActed(false);
        }
        // Start with player after small blind
        currentPlayerIndex = (smallBlindIndex + 1) % players.size();
        // Skip folded players
        while (players.get(currentPlayerIndex).getHasFolded()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        lastRaisePlayerIndex = -1;
    }
    
    /**
     * Check if the current betting round is complete
     */
    public boolean isBettingRoundComplete() {
        // If there's only one player not folded, betting is complete
        int activePlayers = 0;
        for (Player player : players) {
            if (!player.getHasFolded()) {
                activePlayers++;
            }
        }
        
        if (activePlayers <= 1) {
            return true;
        }
        
        // Check if all active players have acted and bets are equal
        boolean allActed = true;
        Long currentBet = null;
        
        // First, find the first active player's bet to use as a reference
        for (Player player : players) {
            if (!player.getHasFolded()) {
                currentBet = player.getCurrentBet();
                break;
            }
        }
        
        // If no active players (shouldn't happen), return true
        if (currentBet == null) {
            return true;
        }
        
        // Now check that all active players have acted and their bets match
        for (Player player : players) {
            if (!player.getHasFolded()) {
                // Consider a player as having acted if they are all-in (can't match the current bet)
                if (!player.getHasActed() && player.getCredit() + player.getCurrentBet() >= currentBet) {
                    allActed = false;
                    break;
                }
                
                if (!currentBet.equals(player.getCurrentBet())) {
                    return false;
                }
            }
        }
        
        return allActed;
    }
    
    /**
     * Move chips from player bets to the pot
     */
    public void collectBetsIntoPot() {
        for (Player player : players) {
            if (pot == null) {
                pot = 0L;
            }
            player.addToTotalBets(player.getCurrentBet());
            pot += player.getCurrentBet();
            player.setCurrentBet(0L);
        }
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

    public void setStatus(GameStatus gameStatus) {
        this.gameStatus=gameStatus;
    }
    public GameStatus getStatus() {
        return gameStatus;
    }

    public Long getCurrentPlayerId() {
        // Check if players list is not empty and index is valid
        if (players != null && !players.isEmpty() && currentPlayerIndex >= 0 && currentPlayerIndex < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            if (currentPlayer != null) {
                return currentPlayer.getUserId(); // Return the user ID of the current player
            }
        }
        // Return null or throw an exception if the current player cannot be determined
        return null; 
    }

    public void setCurrentPlayerId(Long currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }
}
