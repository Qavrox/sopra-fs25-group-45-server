package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.isPublic;

public class GameGetDTO {
    
  private Long id;
  private String password;
  private Boolean isPublic;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getIsPublic(){
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic){
    this.isPublic=isPublic;
  }
    
}
