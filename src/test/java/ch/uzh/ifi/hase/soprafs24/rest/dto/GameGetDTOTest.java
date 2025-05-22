package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game; // Import Game entity
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameGetDTOTest {

    @Test
    public void testGettersAndSetters() {
        GameGetDTO dto = new GameGetDTO();

        Long id = 1L;
        Long creatorId = 2L;
        String password = "securePassword";
        Boolean isPublic = true;
        int maximalPlayers = 6;
        int startCredit = 1000;
        int smallBlind = 10;
        int bigBlind = 20;
        GameStatus gameStatus = GameStatus.WAITING;
        int pot = 500;
        int callAmount = 100;
        int smallBlindIndex = 0;
        int numberOfPlayers = 4;
        List<String> communityCards = Arrays.asList("AS", "KD", "QH");

        // Create a dummy Game to satisfy Player constructor requirements
        Game dummyGameForPlayer = new Game();
        dummyGameForPlayer.setStartCredit(1000L); // Set required field for Player constructor

        Player playerInstance = new Player(3L, new ArrayList<>(), dummyGameForPlayer); // Use public constructor
        playerInstance.setId(300L); // Set the Player's own ID (@Id field) for testing
        List<Player> players = Arrays.asList(playerInstance);
        Long currentPlayerId = playerInstance.getId(); // Example: current player is this one

        dto.setId(id);
        dto.setCreatorId(creatorId);
        dto.setPassword(password);
        dto.setIsPublic(isPublic);
        dto.setMaximalPlayers(maximalPlayers);
        dto.setStartCredit(startCredit);
        dto.setSmallBlind(smallBlind);
        dto.setBigBlind(bigBlind);
        dto.setGameStatus(gameStatus);
        dto.setPot(pot);
        dto.setCallAmount(callAmount);
        dto.setSmallBlindIndex(smallBlindIndex);
        dto.setNumberOfPlayers(numberOfPlayers);
        dto.setCommunityCards(communityCards);
        dto.setPlayers(players);
        dto.setCurrentPlayerId(currentPlayerId);

        assertEquals(id, dto.getId());
        assertEquals(creatorId, dto.getCreatorId());
        assertEquals(password, dto.getPassword());
        assertEquals(isPublic, dto.getIsPublic());
        assertEquals(maximalPlayers, dto.getMaximalPlayers());
        assertEquals(startCredit, dto.getStartCredit());
        assertEquals(smallBlind, dto.getSmallBlind());
        assertEquals(bigBlind, dto.getBigBlind());
        assertEquals(gameStatus, dto.getGameStatus());
        assertEquals(pot, dto.getPot());
        assertEquals(callAmount, dto.getCallAmount());
        assertEquals(smallBlindIndex, dto.getSmallBlindIndex());
        assertEquals(numberOfPlayers, dto.getNumberOfPlayers());
        assertEquals(communityCards, dto.getCommunityCards());
        assertEquals(players, dto.getPlayers());
        assertEquals(currentPlayerId, dto.getCurrentPlayerId());
    }

    @Test
    public void testDefaultValues() {
        GameGetDTO dto = new GameGetDTO();

        assertNull(dto.getId());
        assertNull(dto.getCreatorId());
        assertNull(dto.getPassword());
        assertNull(dto.getIsPublic());
        assertEquals(0, dto.getMaximalPlayers());
        assertEquals(0, dto.getStartCredit());
        assertEquals(0, dto.getSmallBlind());
        assertEquals(0, dto.getBigBlind());
        assertNull(dto.getGameStatus());
        assertEquals(0, dto.getPot());
        assertEquals(0, dto.getCallAmount());
        assertEquals(0, dto.getSmallBlindIndex());
        assertEquals(0, dto.getNumberOfPlayers());
        assertNull(dto.getCommunityCards());
        assertNull(dto.getPlayers());
        assertNull(dto.getCurrentPlayerId());
    }

    @Test
    public void testDifferentInstances() {
        GameGetDTO dto1 = new GameGetDTO();
        dto1.setId(1L);
        dto1.setPassword("game1");
        dto1.setMaximalPlayers(4);

        GameGetDTO dto2 = new GameGetDTO();
        dto2.setId(2L);
        dto2.setPassword("game2");
        dto2.setMaximalPlayers(8);

        // Assert values for dto1
        assertEquals(1L, dto1.getId());
        assertEquals("game1", dto1.getPassword());
        assertEquals(4, dto1.getMaximalPlayers());

        // Assert values for dto2
        assertEquals(2L, dto2.getId());
        assertEquals("game2", dto2.getPassword());
        assertEquals(8, dto2.getMaximalPlayers());

        // Ensure they are different
        assertNotEquals(dto1.getId(), dto2.getId());
        assertNotEquals(dto1.getPassword(), dto2.getPassword());
        assertNotEquals(dto1.getMaximalPlayers(), dto2.getMaximalPlayers());
    }

    @Test
    public void testSetNullValues() {
        GameGetDTO dto = new GameGetDTO();

        dto.setId(null);
        dto.setCreatorId(null);
        dto.setPassword(null);
        dto.setIsPublic(null);
        // int fields cannot be set to null, they will have default 0
        dto.setGameStatus(null);
        dto.setCommunityCards(null);
        dto.setPlayers(null);
        dto.setCurrentPlayerId(null);

        assertNull(dto.getId());
        assertNull(dto.getCreatorId());
        assertNull(dto.getPassword());
        assertNull(dto.getIsPublic());
        assertNull(dto.getGameStatus());
        assertNull(dto.getCommunityCards());
        assertNull(dto.getPlayers());
        assertNull(dto.getCurrentPlayerId());
    }

    @Test
    public void testListHandling() {
        GameGetDTO dto = new GameGetDTO();
        
        List<String> communityCards = new ArrayList<>();
        communityCards.add("AH");
        dto.setCommunityCards(communityCards);
        assertEquals(communityCards, dto.getCommunityCards());
        assertEquals(1, dto.getCommunityCards().size());

        // Create a dummy Game to satisfy Player constructor requirements
        Game dummyGameForPlayer = new Game();
        dummyGameForPlayer.setStartCredit(0L); // Set required field

        List<Player> playerList = new ArrayList<>();
        Player player1 = new Player(10L, new ArrayList<>(), dummyGameForPlayer); // Use public constructor
        player1.setId(10L); // Set Player's own ID (@Id field)
        playerList.add(player1);
        dto.setPlayers(playerList);
        assertEquals(playerList, dto.getPlayers());
        assertEquals(1, dto.getPlayers().size());
        assertEquals(10L, dto.getPlayers().get(0).getId());
    }
}