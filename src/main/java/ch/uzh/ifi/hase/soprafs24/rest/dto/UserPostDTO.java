package ch.uzh.ifi.hase.soprafs24.rest.dto;


import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserProfileDTO.ExperienceLevel;


public class UserPostDTO {

  private String name;
  private String username;
  private String password;
  private LocalDate birthday;
  private int profileImage;
  private ExperienceLevel experienceLevel;

  

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

  public ExperienceLevel getExperienceLevel() {
    return experienceLevel;
  }

  public void setExperienceLevel(ExperienceLevel experienceLevel) {
    this.experienceLevel = experienceLevel;
  }


}
