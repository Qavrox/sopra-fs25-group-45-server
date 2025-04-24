package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.Card;
import ch.uzh.ifi.hase.soprafs24.constant.Deck;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    /**
     * Helper method to create a game with basic configuration
     */
    private Game createTestGame(boolean isPublic) {
        Game game = new Game();
        game.setCreatorId(1L);
        game.setIsPublic(isPublic);
        game.setMaximalPlayers(6);
        game.setStartCredit(1000L);
        game.setGameStatus(GameStatus.WAITING);
        game.setPot(0L);
        game.setCallAmount(0L);
        game.setMaximalPlayers(5);
        
        // Initialize deck and community cards
        game.initializeShuffledDeck();
        game.setCommunityCards(new ArrayList<>());
        
        return game;
    }

    @Test
    void testSaveGame() {
        // Create and save a new game
        Game newGame = createTestGame(true);
        Game savedGame = gameRepository.save(newGame);
        
        // Flush to ensure the entity is actually saved
        entityManager.flush();
        
        // Verify the game was saved with an ID
        assertNotNull(savedGame.getId());
    }

    @Test
    void testFindGameById() {
        // Create and save a new game
        Game newGame = createTestGame(true);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Find the game by ID using findByid (lowercase 'i')
        Game foundGame = gameRepository.findByid(newGame.getId());
        
        // Verify the game was found
        assertNotNull(foundGame);
        assertEquals(newGame.getId(), foundGame.getId());
    }
    
    @Test
    void testFindGameByIdUsingStandardMethod() {
        // Create and save a new game
        Game newGame = createTestGame(true);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Find the game by ID using standard JpaRepository method
        Optional<Game> foundGame = gameRepository.findById(newGame.getId());
        
        // Verify the game was found
        assertTrue(foundGame.isPresent());
        assertEquals(newGame.getId(), foundGame.get().getId());
    }

    @Test
    void testFindAllGames() {
        // Create and save multiple games
        Game game1 = createTestGame(true);
        Game game2 = createTestGame(false);
        Game game3 = createTestGame(true);
        
        entityManager.persist(game1);
        entityManager.persist(game2);
        entityManager.persist(game3);
        entityManager.flush();
        
        // Find all games
        List<Game> allGames = gameRepository.findAll();
        
        // Verify all games were found
        assertNotNull(allGames);
        assertTrue(allGames.size() >= 3);
    }

    @Test
    void testUpdateGame() {
        // Create and save a new game
        Game newGame = createTestGame(true);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Retrieve the saved game
        Game savedGame = gameRepository.findByid(newGame.getId());
        
        // Update game properties
        savedGame.setGameStatus(GameStatus.PREFLOP);
        savedGame.setPot(500L);
        savedGame.setCallAmount(100L);
        
        // Save the updated game
        gameRepository.save(savedGame);
        entityManager.flush();
        
        // Retrieve the updated game
        Game updatedGame = gameRepository.findByid(newGame.getId());
        
        // Verify the game was updated
        assertEquals(GameStatus.PREFLOP, updatedGame.getGameStatus());
        assertEquals(500L, updatedGame.getPot());
        assertEquals(100L, updatedGame.getCallAmount());
    }

    @Test
    void testDeleteGame() {
        // Create and save a new game
        Game newGame = createTestGame(true);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Delete the game
        gameRepository.deleteById(newGame.getId());
        entityManager.flush();
        
        // Try to find the deleted game
        Game deletedGame = gameRepository.findByid(newGame.getId());
        
        // Verify the game was deleted
        assertNull(deletedGame);
    }

    @Test
    void testPersistenceOfCardDeck() {
        // Create a game with initialized deck
        Game newGame = createTestGame(true);
        
        // Get initial deck size
        int initialDeckSize = newGame.getCardDeck().size();
        
        // Draw some cards
        Card _card1 = newGame.drawRandomCard();
        Card _card2 = newGame.drawRandomCard();
        
        // Verify deck size reduced
        assertEquals(initialDeckSize - 2, newGame.getCardDeck().size());
        
        // Save the game with modified deck
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Retrieve the saved game
        Game savedGame = gameRepository.findByid(newGame.getId());
        
        // Verify deck was persisted with correct size
        assertEquals(initialDeckSize - 2, savedGame.getCardDeck().size());
        
        // Convert to Deck object and verify
        Deck savedDeck = savedGame.getDeck();
        assertEquals(initialDeckSize - 2, savedDeck.remainingCards());
    }

    @Test
    void testPersistenceOfCommunityCards() {
        // Create a game with empty community cards
        Game newGame = createTestGame(true);
        
        // Add community cards
        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        Card card2 = new Card(Card.Suit.SPADES, Card.Rank.KING);
        newGame.addCommunityCard(card1);
        newGame.addCommunityCard(card2);
        
        // Verify community cards count
        assertEquals(2, newGame.getCommunityCards().size());
        
        // Save the game
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Retrieve the saved game
        Game savedGame = gameRepository.findByid(newGame.getId());
        
        // Verify community cards were persisted
        assertEquals(2, savedGame.getCommunityCards().size());
        
        // Verify cards as objects
        List<Card> communityCards = savedGame.getCommunityCardsAsObjects();
        assertEquals(2, communityCards.size());
        assertEquals(Card.Suit.HEARTS, communityCards.get(0).getSuit());
        assertEquals(Card.Rank.ACE, communityCards.get(0).getRank());
        assertEquals(Card.Suit.SPADES, communityCards.get(1).getSuit());
        assertEquals(Card.Rank.KING, communityCards.get(1).getRank());
    }
    
    @Test
    void testGameStatusLifecycle() {
        // Create a game
        Game newGame = createTestGame(true);
        newGame.setGameStatus(GameStatus.WAITING);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Progress through game states
        Game game = gameRepository.findByid(newGame.getId());
        game.setGameStatus(GameStatus.READY);
        gameRepository.save(game);
        entityManager.flush();
        
        game = gameRepository.findByid(newGame.getId());
        assertEquals(GameStatus.READY, game.getGameStatus());
        
        game.setGameStatus(GameStatus.PREFLOP);
        gameRepository.save(game);
        entityManager.flush();
        
        game = gameRepository.findByid(newGame.getId());
        assertEquals(GameStatus.PREFLOP, game.getGameStatus());
        
        // Test progressing through all poker rounds
        GameStatus[] gameStates = new GameStatus[] {
            GameStatus.FLOP, GameStatus.TURN, GameStatus.RIVER, 
            GameStatus.SHOWDOWN, GameStatus.GAMEOVER
        };
        
        for (GameStatus status : gameStates) {
            game.setGameStatus(status);
            gameRepository.save(game);
            entityManager.flush();
            
            game = gameRepository.findByid(newGame.getId());
            assertEquals(status, game.getGameStatus());
        }
    }

    @Test
    void testGameWithPlayers() {
        // Create a new game
        Game newGame = createTestGame(true);
        entityManager.persist(newGame);
        entityManager.flush();
        
        // Retrieve the saved game
        Game savedGame = gameRepository.findByid(newGame.getId());
        
        // Create and add players to the game
        List<String> hand1 = new ArrayList<>();
        List<String> hand2 = new ArrayList<>();
        
        Player player1 = new Player(1L, hand1, savedGame);
        Player player2 = new Player(2L, hand2, savedGame);
        
        savedGame.addPlayer(player1);
        savedGame.addPlayer(player2);
        
        // Save the game with players
        gameRepository.save(savedGame);
        entityManager.flush();
        
        // Retrieve the updated game
        Game gameWithPlayers = gameRepository.findByid(savedGame.getId());
        
        // Verify players were saved correctly
        assertNotNull(gameWithPlayers.getPlayers());
        assertEquals(2, gameWithPlayers.getPlayers().size());
        assertEquals(2, gameWithPlayers.getNumberOfPlayers());
        
        // Verify player properties
        List<Player> players = gameWithPlayers.getPlayers();
        assertEquals(1L, players.get(0).getUserId());
        assertEquals(2L, players.get(1).getUserId());
        
        // Test removing a player
        gameWithPlayers.removePlayer(players.get(0));
        gameRepository.save(gameWithPlayers);
        entityManager.flush();
        
        // Verify player was removed
        Game gameAfterRemoval = gameRepository.findByid(savedGame.getId());
        assertEquals(1, gameAfterRemoval.getPlayers().size());
        assertEquals(1, gameAfterRemoval.getNumberOfPlayers());
    }

}
