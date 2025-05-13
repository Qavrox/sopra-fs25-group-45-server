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
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    public GameHistoryService(
            @Qualifier("gameHistoryRepository") GameHistoryRepository gameHistoryRepository,
            @Qualifier("userRepository") UserRepository userRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
        this.userRepository = userRepository;
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
        history.setPlayedAt(LocalDateTime.now());
        history.setResult(result);
        history.setWinnings(winnings);
        history.setOtherPlayerIds(otherPlayerIds);
        
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
     * Get statistics for a user
     * @param userId - ID of the user
     * @return UserStatisticsDTO with calculated statistics
     */
    public UserStatisticsDTO getUserStatistics(Long userId) {
        // Get user
        User user = userRepository.findByid(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Get game history
        List<GameHistory> history = gameHistoryRepository.findByUserId(userId);
        
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
        List<Object[]> results = gameHistoryRepository.findTopPlayersByWinnings();
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        long rank = 1;
        for (Object[] result : results) {
            Long userId = (Long) result[0];
            Long totalWinnings = ((Number) result[1]).longValue();
            Long gamesPlayed = ((Number) result[2]).longValue();
            
            User user = userRepository.findByid(userId);
            if (user != null) {
                LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
                entry.setUserId(userId);
                entry.setUsername(user.getUsername());
                entry.setDisplayName(user.getName());
                entry.setTotalWinnings(totalWinnings);
                entry.setGamesPlayed(gamesPlayed);
                entry.setRank(rank++);
                
                leaderboard.add(entry);
            }
        }
        
        return leaderboard;
    }

    /**
     * Get leaderboard by win rate
     * @return list of LeaderboardEntryDTO sorted by win rate
     */
    public List<LeaderboardEntryDTO> getLeaderboardByWinRate() {
        List<Object[]> results = gameHistoryRepository.findTopPlayersByWinRate();
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        
        long rank = 1;
        for (Object[] result : results) {
            Long userId = (Long) result[0];
            Long wins = ((Number) result[1]).longValue();
            Long gamesPlayed = ((Number) result[2]).longValue();
            
            double winRate = (double) wins / gamesPlayed * 100;
            
            User user = userRepository.findByid(userId);
            if (user != null) {
                LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
                entry.setUserId(userId);
                entry.setUsername(user.getUsername());
                entry.setDisplayName(user.getName());
                entry.setWinRate(winRate);
                entry.setGamesPlayed(gamesPlayed);
                entry.setRank(rank++);
                
                leaderboard.add(entry);
            }
        }
        
        return leaderboard;
    }
}
