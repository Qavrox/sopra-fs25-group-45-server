package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import javassist.tools.framedump;
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

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserFriends friends;

  @Column(nullable = true)
  private Long gamesPlayed = 0L;
  
  @Column(nullable = true)
  private Long gamesWon = 0L;
  
  @Column(nullable = true)
  private Double winRate = 0.0;
  
  @Column(nullable = true)
  private Long totalWinnings = 0L;

  public UserFriends getFriends() {
    return friends;
  }

  public void setFriends(UserFriends friends) {
    this.friends = friends;
  }

  @Column(nullable = true)
  private UserLevel experienceLevel;

  @Column(nullable = false)
  private LocalDate creationDate;

  @Column(nullable = true)
  private LocalDate birthday;

  @Column(nullable = false)
  private int profileImage = 0;

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

  public void setexperienceLevel(UserLevel experienceLevel) {
    this.experienceLevel = experienceLevel;
  }

  public UserLevel getexperienceLevel() {
    return experienceLevel;
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

  public int getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(int profileImage) {
    this.profileImage = profileImage;
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

  @PostPersist
  private void ensureFriends() {
    // automatically generate new friends field
    if (this.friends == null) {
          this.friends = new UserFriends();
          this.friends.setUser(this);
    }
  }

  public Long getGamesPlayed() {
    return gamesPlayed;
  }

  public void setGamesPlayed(Long gamesPlayed) {
    this.gamesPlayed = gamesPlayed;
  }

  public Long getGamesWon() {
    return gamesWon;
  }

  public void setGamesWon(Long gamesWon) {
    this.gamesWon = gamesWon;
  }

  public Double getWinRate() {
    return winRate;
  }

  public void setWinRate(Double winRate) {
    this.winRate = winRate;
  }

  public Long getTotalWinnings() {
    return totalWinnings;
  }

  public void setTotalWinnings(Long totalWinnings) {
    this.totalWinnings = totalWinnings;
  }
  
  public void updateWinRate() {
    if (this.gamesPlayed > 0) {
      this.winRate = (double) this.gamesWon / this.gamesPlayed * 100;
    } else {
      this.winRate = 0.0;
    }
  }
}
