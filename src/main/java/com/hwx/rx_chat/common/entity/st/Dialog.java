package com.hwx.rx_chat.common.entity.st;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private List<UserEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "msgDialog")
    private List<Message> messages = new ArrayList<>();

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


    public List<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<UserEntity> members) {
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
                Objects.equals(createDate, dialog.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userCreated, members, messages, createDate);
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userCreated=" + userCreated +
                ", members=" + members +
                ", messages=" + messages +
                ", createDate=" + createDate +
                '}';
    }
}
