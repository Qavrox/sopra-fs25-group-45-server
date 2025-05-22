package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GameHistoryRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId; // Import ZoneId
import java.time.ZonedDateTime; // Import ZonedDateTime
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.service.UserFriendsService; // Added import
import java.util.Comparator; // Added import
import java.util.stream.Collectors; // Added import

/**
 * GameHistoryService
 * This class is the "worker" and responsible for all functionality related to game history
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final UserRepository userRepository;
    private final UserFriendsService userFriendsService; // Added UserFriendsService

    @Autowired
    public GameHistoryService(
            @Qualifier("gameHistoryRepository") GameHistoryRepository gameHistoryRepository,
            @Qualifier("userRepository") UserRepository userRepository,
            UserFriendsService userFriendsService) { // Added UserFriendsService to constructor
        this.gameHistoryRepository = gameHistoryRepository;
        this.userRepository = userRepository;
        this.userFriendsService = userFriendsService; // Initialize UserFriendsService
    }

    /**
     * Record a game result for a user
     * @param userId - ID of the user
     * @param gameId - ID of the game
     * @param result - "Win" or "Loss"
     * @param winnings - amount won or lost (positive for win, negative for loss)
     * @param otherPlayerIds - IDs of other players in the game
     * @return the created GameHistory entity
     */
    public GameHistory recordGameResult(Long userId, Long gameId, String result, Long winnings, List<Long> otherPlayerIds) {
        // Validate input
        if (userId == null || gameId == null || result == null || winnings == null) {
            throw new IllegalArgumentException("All required fields must be provided");
        }
        
        // Create new game history record
        GameHistory history = new GameHistory();
        history.setUserId(userId);
        history.setGameId(gameId);

        // Get current time in server's default timezone (e.g., Swiss time)
        LocalDateTime localNow = LocalDateTime.now();
        // Convert local time to ZonedDateTime in server's default timezone
        ZonedDateTime localZonedNow = localNow.atZone(ZoneId.systemDefault());
        // Convert to UTC
        ZonedDateTime utcZonedNow = localZonedNow.withZoneSameInstant(ZoneId.of("UTC"));
        // Get LocalDateTime representation of UTC time
        LocalDateTime utcNow = utcZonedNow.toLocalDateTime();
        
        history.setPlayedAt(utcNow); // Save as UTC time
        history.setResult(result);
        history.setWinnings(winnings);
        history.setOtherPlayerIds(otherPlayerIds);

        // Update user statistics
        User user = userRepository.findByid(userId);
        if (user != null) {
        // Update games played
        Long gamesPlayed = user.getGamesPlayed();
        if (gamesPlayed == null) gamesPlayed = 0L;
        user.setGamesPlayed(gamesPlayed + 1);

        // Update games won
        if ("Win".equals(result)) {
            Long gamesWon = user.getGamesWon();
            if (gamesWon == null) gamesWon = 0L;
            user.setGamesWon(gamesWon + 1);
        }
        // Update total winnings
        Long totalWinnings = user.getTotalWinnings();
        if (totalWinnings == null) totalWinnings = 0L;
        user.setTotalWinnings(totalWinnings + winnings);
        // Update win rate
        user.updateWinRate();
        userRepository.save(user);
    }
        
        // Save and return
        return gameHistoryRepository.save(history);
    }

    /**
     * Get all game history records for a user
     * @param userId - ID of the user
     * @return list of GameHistory entities
     */
    public List<GameHistory> getUserGameHistory(Long userId) {
        return gameHistoryRepository.findByUserId(userId);
    }

    /**
     * Get game history records for a user within a time range
     * @param userId - ID of the user
     * @param startDate - start date of the range
     * @param endDate - end date of the range
     * @return list of GameHistory entities
     */
    public List<GameHistory> getUserGameHistoryByTimeRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return gameHistoryRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate);
    }

    /**
     * Get statistics for a user, optionally filtered by a time range.
     * @param userId - ID of the user
     * @param startDate - Optional start date of the range
     * @param endDate - Optional end date of the range
     * @return UserStatisticsDTO with calculated statistics
     */
    public UserStatisticsDTO getUserStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Get user
        User user = userRepository.findByid(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Get game history
        List<GameHistory> history;
        if (startDate != null && endDate != null) {
            history = gameHistoryRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate);
        } else {
            history = gameHistoryRepository.findByUserId(userId);
        }
        
        // Calculate statistics
        long totalGames = history.size();
        long wins = history.stream().filter(h -> "Win".equals(h.getResult())).count();
        double winRate = totalGames > 0 ? (double) wins / totalGames * 100 : 0;
        long totalWinnings = history.stream().mapToLong(GameHistory::getWinnings).sum();
        
        // Create DTO
        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setUserId(userId);
        stats.setUsername(user.getUsername());
        stats.setDisplayName(user.getName());
        stats.setGamesPlayed(totalGames);
        stats.setWins(wins);
        stats.setLosses(totalGames - wins);
        stats.setWinRate(winRate);
        stats.setTotalWinnings(totalWinnings);
        stats.setAveragePosition(3.0); // Placeholder - would need position data to calculate
        
        return stats;
    }

    /**
     * Get leaderboard by total winnings
     * @return list of LeaderboardEntryDTO sorted by total winnings
     */
    public List<LeaderboardEntryDTO> getLeaderboardByWinnings() {
        List<User> users = userRepository.findAll();
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        // ranking
        users.sort((u1, u2) -> Long.compare(
            u2.getTotalWinnings() != null ? u2.getTotalWinnings() : 0L,
            u1.getTotalWinnings() != null ? u1.getTotalWinnings() : 0L
        ));
        
        long rank = 1;
        for (User user : users) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setUserId(user.getId());
            entry.setUsername(user.getUsername());
            entry.setDisplayName(user.getName());
            entry.setWinRate(user.getWinRate());
            entry.setTotalWinnings(user.getTotalWinnings());
            entry.setGamesPlayed(user.getGamesPlayed());
            entry.setRank(rank++);
            
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }

    /**
     * Get leaderboard by win rate
     * @return list of LeaderboardEntryDTO sorted by win rate
     */
    public List<LeaderboardEntryDTO> getLeaderboardByWinRate() {
        List<User> users = userRepository.findByGamesPlayedGreaterThanEqual(1L);
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        // ranking
        users.sort((u1, u2) -> Double.compare(u2.getWinRate(), u1.getWinRate()));
        
        long rank = 1;
        for (User user : users) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setUserId(user.getId());
            entry.setUsername(user.getUsername());
            entry.setDisplayName(user.getName());
            entry.setWinRate(user.getWinRate());
            entry.setTotalWinnings(user.getTotalWinnings());
            entry.setGamesPlayed(user.getGamesPlayed());
            entry.setRank(rank++);
            
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }

    /**
     * Get friend leaderboard by total winnings
     * @param currentUserId - ID of the current user
     * @return list of LeaderboardEntryDTO for friends sorted by total winnings
     */
    public List<LeaderboardEntryDTO> getFriendLeaderboardByWinnings(Long currentUserId) {
        User currentUser = userRepository.findByid(currentUserId);
        if (currentUser == null) {
            throw new IllegalArgumentException("Current user not found");
        }

        List<User> friends = userFriendsService.getFriends(currentUserId);
        // Also include the current user in their own friend leaderboard
        if (friends.stream().noneMatch(friend -> friend.getId().equals(currentUserId))) {
            friends.add(currentUser);
        }
        
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        // Sort friends by total winnings
        friends.sort(Comparator.comparing(User::getTotalWinnings, Comparator.nullsLast(Comparator.reverseOrder())));
        
        long rank = 1;
        for (User user : friends) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setUserId(user.getId());
            entry.setUsername(user.getUsername());
            entry.setDisplayName(user.getName());
            entry.setWinRate(user.getWinRate());
            entry.setTotalWinnings(user.getTotalWinnings() != null ? user.getTotalWinnings() : 0L);
            entry.setGamesPlayed(user.getGamesPlayed() != null ? user.getGamesPlayed() : 0L);
            entry.setRank(rank++);
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }

    /**
     * Get friend leaderboard by win rate
     * @param currentUserId - ID of the current user
     * @return list of LeaderboardEntryDTO for friends sorted by win rate
     */
    public List<LeaderboardEntryDTO> getFriendLeaderboardByWinRate(Long currentUserId) {
        User currentUser = userRepository.findByid(currentUserId);
        if (currentUser == null) {
            throw new IllegalArgumentException("Current user not found");
        }

        List<User> friends = userFriendsService.getFriends(currentUserId);
         // Also include the current user in their own friend leaderboard
        if (friends.stream().noneMatch(friend -> friend.getId().equals(currentUserId))) {
            friends.add(currentUser);
        }

        // Filter friends who have played at least 1 game
        List<User> eligibleFriends = friends.stream()
            .filter(user -> user.getGamesPlayed() != null && user.getGamesPlayed() >= 1L)
            .collect(Collectors.toList());

        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        // Sort eligible friends by win rate
        eligibleFriends.sort(Comparator.comparing(User::getWinRate, Comparator.nullsLast(Comparator.reverseOrder())));
        
        long rank = 1;
        for (User user : eligibleFriends) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setUserId(user.getId());
            entry.setUsername(user.getUsername());
            entry.setDisplayName(user.getName());
            entry.setWinRate(user.getWinRate());
            entry.setTotalWinnings(user.getTotalWinnings() != null ? user.getTotalWinnings() : 0L);
            entry.setGamesPlayed(user.getGamesPlayed());
            entry.setRank(rank++);
            leaderboard.add(entry);
        }
        
        return leaderboard;
    }
}
