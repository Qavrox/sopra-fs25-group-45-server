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
        if (!requestingUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own game history");
        }
        
        // Parse dates
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (java.time.format.DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start date format. Please use ISO_DATE_TIME: " + startDateStr);
        }
        try {
            endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (java.time.format.DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid end date format. Please use ISO_DATE_TIME: " + endDateStr);
        }
        
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
     * Get user statistics, optionally filtered by a time range.
     * @param userId - ID of the user
     * @param token - authentication token
     * @param startDateStr - Optional start date string (ISO format, e.g., "2023-01-01T00:00:00")
     * @param endDateStr - Optional end date string (ISO format, e.g., "2023-01-31T23:59:59")
     * @return UserStatisticsDTO
     */
    @GetMapping("/users/{userId}/statistics")
    @ResponseStatus(HttpStatus.OK)
    public UserStatisticsDTO getUserStatistics(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr) {
        
        // Validate user authentication
        User requestingUser = userService.getUserByToken(token);
        if (!requestingUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own statistics");
        }
        
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start date format. Please use ISO_DATE_TIME.");
            }
        }
        
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid end date format. Please use ISO_DATE_TIME.");
            }
        }

        // If only one date is provided, throw a BAD_REQUEST error.
        // Both dates must be provided if a time range filter is intended.
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both startDate and endDate must be provided for a time range filter, or neither to get all-time statistics.");
        }
        
        // Get statistics
        return gameHistoryService.getUserStatistics(userId, startDate, endDate);
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

    /**
     * Get friend leaderboard by winnings
     * @param token - authentication token
     * @return list of LeaderboardEntryDTO
     */
    @GetMapping("/leaderboard/friends/winnings")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaderboardEntryDTO> getFriendLeaderboardByWinnings(
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        User requestingUser = userService.getUserByToken(token);
        if (requestingUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        
        // Get friend leaderboard
        return gameHistoryService.getFriendLeaderboardByWinnings(requestingUser.getId());
    }

    /**
     * Get friend leaderboard by win rate
     * @param token - authentication token
     * @return list of LeaderboardEntryDTO
     */
    @GetMapping("/leaderboard/friends/winrate")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaderboardEntryDTO> getFriendLeaderboardByWinRate(
            @RequestHeader("Authorization") String token) {
        
        // Validate user authentication
        User requestingUser = userService.getUserByToken(token);
        if (requestingUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        
        // Get friend leaderboard
        return gameHistoryService.getFriendLeaderboardByWinRate(requestingUser.getId());
    }
}