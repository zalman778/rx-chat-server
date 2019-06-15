package com.hwx.rx_chat.common.entity.st;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(
     name="user"
    ,uniqueConstraints={
        @UniqueConstraint(columnNames={"id"}),
        @UniqueConstraint(columnNames={"username"}),
        @UniqueConstraint(columnNames={"mail"})
    }
  )
public class UserEntity implements Serializable {

    @Id
    private String id;

    private String username;

    @Column(name="password_hash", length = 256)
    private String passwordHash;

    @Column(name="reg_date")
    private Date regDate;

    @Column(name="is_active")
    private boolean isActive;

    private String mail;

    @Column(name="avatar_url")
    private String avatarUrl;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String bio;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable (name="dialog_members",
            joinColumns=@JoinColumn (name="dialog_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private List<Dialog> dialogs = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Friendship> friends = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "requester")
    private Set<Friendship> friendRequests = new HashSet<>();



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Friendship> getFriends() {
        return friends;
    }

    public void setFriends(Set<Friendship> friends) {
        this.friends = friends;
    }

    public Set<Friendship> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Set<Friendship> friendRequests) {
        this.friendRequests = friendRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return isActive == that.isActive &&
                Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(passwordHash, that.passwordHash) &&
                Objects.equals(regDate, that.regDate) &&
                Objects.equals(mail, that.mail) &&
                Objects.equals(avatarUrl, that.avatarUrl) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(bio, that.bio) &&
                Objects.equals(dialogs, that.dialogs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash, regDate, isActive, mail, avatarUrl, firstName, lastName, bio, dialogs);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", regDate=" + regDate +
                ", isActive=" + isActive +
                ", mail='" + mail + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bio='" + bio + '\'' +
                ", dialogs=" + dialogs +
                '}';
    }
}
