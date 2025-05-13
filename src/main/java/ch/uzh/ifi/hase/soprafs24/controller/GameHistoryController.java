package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameHistoryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserStatisticsDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GameHistory Controller
 * This class is responsible for handling all REST request that are related to game history.
 * The controller will receive the request and delegate the execution to the GameHistoryService.
 */
@RestController
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;
    private final UserService userService;

    GameHistoryController(GameHistoryService gameHistoryService, UserService userService) {
        this.gameHistoryService = gameHistoryService;
        this.userService = userService;
    }

    /**
     * Get user game history
     * @param userId - ID of the user
     * @param token - authentication token
     * @return list of GameHistoryDTO
     */
    @GetMapping("/users/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    public List<GameHistoryDTO> getUserGameHistory(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        User requestingUser = userService.getUserByToken(token);
        if (!requestingUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own game history");
        }
        
        // Get game history
        List<GameHistory> gameHistories = gameHistoryService.getUserGameHistory(userId);
        
        // Convert to DTOs
        List<GameHistoryDTO> gameHistoryDTOs = new ArrayList<>();
        for (GameHistory gameHistory : gameHistories) {
            gameHistoryDTOs.add(DTOMapper.INSTANCE.convertEntityToGameHistoryDTO(gameHistory));
        }
        
        return gameHistoryDTOs;
    }

    /**
     * Get user game history by time range
     * @param userId - ID of the user
     * @param startDateStr - start date string (ISO format)
     * @param endDateStr - end date string (ISO format)
     * @param token - authentication token
     * @return list of GameHistoryDTO
     */
    @GetMapping("/users/{userId}/history/range")
    @ResponseStatus(HttpStatus.OK)
    public List<GameHistoryDTO> getUserGameHistoryByTimeRange(
            @PathVariable Long userId,
            @RequestParam String startDateStr,
            @RequestParam String endDateStr,
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        User requestingUser = userService.getUserByToken(token);
        
        // Parse dates
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ISO_DATE_TIME);
        
        // Get game history
        List<GameHistory> gameHistories = gameHistoryService.getUserGameHistoryByTimeRange(userId, startDate, endDate);
        
        // Convert to DTOs
        List<GameHistoryDTO> gameHistoryDTOs = new ArrayList<>();
        for (GameHistory gameHistory : gameHistories) {
            gameHistoryDTOs.add(DTOMapper.INSTANCE.convertEntityToGameHistoryDTO(gameHistory));
        }
        
        return gameHistoryDTOs;
    }

    /**
     * Get user statistics
     * @param userId - ID of the user
     * @param token - authentication token
     * @return UserStatisticsDTO
     */
    @GetMapping("/users/{userId}/statistics")
    @ResponseStatus(HttpStatus.OK)
    public UserStatisticsDTO getUserStatistics(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        userService.getUserByToken(token);
        
        // Get statistics
        return gameHistoryService.getUserStatistics(userId);
    }

    /**
     * Get leaderboard by winnings
     * @param token - authentication token
     * @return list of LeaderboardEntryDTO
     */
    @GetMapping("/leaderboard/winnings")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaderboardEntryDTO> getLeaderboardByWinnings(
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        userService.getUserByToken(token);
        
        // Get leaderboard
        return gameHistoryService.getLeaderboardByWinnings();
    }

    /**
     * Get leaderboard by win rate
     * @param token - authentication token
     * @return list of LeaderboardEntryDTO
     */
    @GetMapping("/leaderboard/winrate")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaderboardEntryDTO> getLeaderboardByWinRate(
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        userService.getUserByToken(token);
        
        // Get leaderboard
        return gameHistoryService.getLeaderboardByWinRate();
    }
}