package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import java.util.List;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = true, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = true)
  private UserLevel level;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = true)
  private LocalDate birthday;

  @Lob
  @Column
  private byte[] profileImage;

  @ManyToMany
  @JoinTable(
      name = "user_friends",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "friend_id")
  )
  private List<User> friends;

  // Friend requests sent by this user
  @ManyToMany
  @JoinTable(
      name = "friend_requests_sent",
      joinColumns = @JoinColumn(name = "sender_id"),
      inverseJoinColumns = @JoinColumn(name = "receiver_id")
  )
  private List<User> sentFriendRequests;

  // Friend requests received by this user
  @ManyToMany
  @JoinTable(
      name = "friend_requests_received",
      joinColumns = @JoinColumn(name = "receiver_id"),
      inverseJoinColumns = @JoinColumn(name = "sender_id")
  )
  private List<User> receivedFriendRequests;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setLevel(UserLevel level) {
    this.level = level;
  }

  public UserLevel getLevel() {
    return level;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public byte[] getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
  }

  public List<User> getFriends() {
    return friends;
  }

  public void setFriends(List<User> friends) {
    this.friends = friends;
  }

  public LocalDate getCreationDate() {
    return creationDate;  
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }
  
  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public List<User> getSentFriendRequests() {
    return sentFriendRequests;
  }

  public void setSentFriendRequests(List<User> sentFriendRequests) {
    this.sentFriendRequests = sentFriendRequests;
  }

  public List<User> getReceivedFriendRequests() {
    return receivedFriendRequests;
  }

  public void setReceivedFriendRequests(List<User> receivedFriendRequests) {
    this.receivedFriendRequests = receivedFriendRequests;
  }
}
