package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserFriendDTO {
    private Long id;
    private String username;
    private UserStatus online;
    private Date createdAt;
    private Date birthday;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public UserStatus getOnline() {
        return online;
    }
    public void setOnline(UserStatus online) {
        this.online = online;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
