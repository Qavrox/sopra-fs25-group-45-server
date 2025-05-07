package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.constant.UserLevel;
import java.time.LocalDate;

public class UserPutDTO {

  private String name;
  private String username;
  private UserLevel experienceLevel;
  private String password;
  private LocalDate birthday;
  private int profileImage;

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

  public UserLevel getexperienceLevel() {
    return experienceLevel;
  }

  public void setexperienceLevel(UserLevel experienceLevel) {
    this.experienceLevel = experienceLevel;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
