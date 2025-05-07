package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import java.time.LocalDate;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private UserLevel experienceLevel;
  private boolean online;
  private LocalDate creationDate;
  private LocalDate birthday;
  private int profileImage;

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

  public boolean getOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public UserLevel getexperienceLevel() {
    return experienceLevel;
  }

  public void setexperienceLevel(UserLevel experienceLevel) {
    this.experienceLevel = experienceLevel;
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

  public int getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(int profileImage) {
    this.profileImage = profileImage;
  }
}
