package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserFriends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userFriendsRepository")
public interface UserFriendsRepository extends JpaRepository<UserFriends, Long> {
    UserFriends findByUserID(long userID);
}
