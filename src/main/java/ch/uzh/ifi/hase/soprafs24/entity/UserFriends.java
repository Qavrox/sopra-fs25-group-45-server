package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER_FRIENDS")
public class UserFriends implements Serializable {
    @Id
    @Column(nullable = false, unique = true)
    private Long userID;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "userID")
    private User user;
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Friend Requests: using a join table to map the relationship.
    @ManyToMany
    @JoinTable(
        name = "USER_FRIEND_REQUESTS",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "requested_friend_id")
    )
    private List<User> friendRequests = new ArrayList<>();
    
    // Friends
    @ManyToMany
    @JoinTable(
        name = "USER_FRIEND_RELATIONS",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_user_id")
    )
    private List<User> friends = new ArrayList<>();

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public List<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
