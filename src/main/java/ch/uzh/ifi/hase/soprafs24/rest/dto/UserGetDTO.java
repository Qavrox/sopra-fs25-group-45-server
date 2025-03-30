package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import java.time.LocalDate;
import java.util.List;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private UserLevel level;
  private UserStatus status;
  private LocalDate creationDate;
  private LocalDate birthday;
  private byte[] profileImage;
  private List<UserGetDTO> friends;
  private List<UserGetDTO> receivedFriendRequests;

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public UserLevel getLevel() {
    return level;
  }

  public void setLevel(UserLevel level) {
    this.level = level;
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

  public byte[] getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
  }

  public List<UserGetDTO> getFriends() {
    return friends;
  }

  public void setFriends(List<UserGetDTO> friends) {
    this.friends = friends;
  }

  public List<UserGetDTO> getReceivedFriendRequests() {
    return receivedFriendRequests;
  }

  public void setReceivedFriendRequests(List<UserGetDTO> receivedFriendRequests) {
    this.receivedFriendRequests = receivedFriendRequests;
  }
  
}
