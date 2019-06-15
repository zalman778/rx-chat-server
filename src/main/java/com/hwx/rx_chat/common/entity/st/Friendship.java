package com.hwx.rx_chat.common.entity.st;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Friendship implements Serializable {

    @Id
//    @GeneratedValue
    private String id;


    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private
    UserEntity user;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private
    UserEntity requester;


    @Temporal(javax.persistence.TemporalType.DATE)
    private
    Date date;

    @Column(nullable = false)
    private
    Boolean active;

    @Column(nullable = false)
    private
    Boolean accepted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public UserEntity getRequester() {
        return requester;
    }

    public void setRequester(UserEntity requester) {
        this.requester = requester;
    }
}
