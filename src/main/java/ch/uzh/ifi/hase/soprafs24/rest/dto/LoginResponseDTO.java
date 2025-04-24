package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LoginResponseDTO {

  private String token;
  private UserProfileDTO user;

  public UserProfileDTO getUser() {
    return user;
  }

  public void setUser(UserProfileDTO userProfile) {
    this.user = userProfile;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}