package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GameHistoryRepository
 * This interface is used to access the game history data in the database.
 * It provides methods to query game history records.
 */
@Repository("gameHistoryRepository")
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    
    /**
     * Find all game history records for a specific user
     */
    List<GameHistory> findByUserId(Long userId);
    
    /**
     * Find game history records for a specific user within a time range
     */
    List<GameHistory> findByUserIdAndPlayedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find top players by total winnings
     */
    @Query("SELECT gh.userId, SUM(gh.winnings) as totalWinnings, COUNT(gh) as gamesPlayed " +
           "FROM GameHistory gh " +
           "GROUP BY gh.userId " +
           "ORDER BY totalWinnings DESC")
    List<Object[]> findTopPlayersByWinnings();
    
    /**
     * Find top players by win rate
     */
    @Query("SELECT gh.userId, " +
           "COUNT(CASE WHEN gh.result = 'Win' THEN 1 END) as wins, " +
           "COUNT(gh) as gamesPlayed " +
           "FROM GameHistory gh " +
           "GROUP BY gh.userId " +
           "HAVING COUNT(gh) >= 5 " + // Minimum 5 games to be ranked
           "ORDER BY (CAST(COUNT(CASE WHEN gh.result = 'Win' THEN 1 END) AS double) / COUNT(gh)) DESC")
    List<Object[]> findTopPlayersByWinRate();
    
    /**
     * Count total games played by a user
     */
    long countByUserId(Long userId);
    
    /**
     * Count wins for a user
     */
    long countByUserIdAndResult(Long userId, String result);
}
