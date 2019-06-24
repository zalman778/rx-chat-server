package com.hwx.rx_chat.common.entity.st;


import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="dialog")
public class Dialog implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_create")
    private UserEntity userCreated;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable (name="dialog_members",
            joinColumns=@JoinColumn (name="dialog_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
            )
    private Set<UserEntity> members = new HashSet<>();

    @OneToMany(mappedBy = "msgDialog")
    private List<Message> messages = new ArrayList<>();

    private String imageUrl;

    private Date createDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(UserEntity userCreated) {
        this.userCreated = userCreated;
    }


    public Set<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<UserEntity> members) {
        this.members = members;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return Objects.equals(id, dialog.id) &&
                Objects.equals(name, dialog.name) &&
                Objects.equals(userCreated, dialog.userCreated) &&
                Objects.equals(members, dialog.members) &&
                Objects.equals(messages, dialog.messages) &&
                Objects.equals(imageUrl, dialog.imageUrl) &&
                Objects.equals(createDate, dialog.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userCreated, members, messages, imageUrl, createDate);
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userCreated=" + userCreated +
                ", members=" + members +
                ", messages=" + messages +
                ", imageUrl='" + imageUrl + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
