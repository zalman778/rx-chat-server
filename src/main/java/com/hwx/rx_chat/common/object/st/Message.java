package com.hwx.rx_chat.common.object.st;

import com.google.gson.annotations.SerializedName;
import com.hwx.rx_chat.common.entity.rx.RxMessage;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="message")
public class Message {

    @Id
    @javax.persistence.Id
    @SerializedName("id")
    private String id;

    @SerializedName("user_from_name")
    @Column(name = "user_from_name")
    private String userFromName;

    @SerializedName("user_from_id")
    @Column(name="user_from_id")
    private String userFromId;

    @SerializedName("value")
    @Column(name = "value")
    private String value;

    @SerializedName("date_sent")
    @Column(name = "date_sent")
    private Date dateSent;

    @SerializedName("date_exp")
    @Column(name = "date_exp")
    private Date dateExp;

    @SerializedName("is_expirable")
    @Column(name = "is_expirable")
    private Boolean isExpirable;

    @SerializedName("is_edited")
    @Column(name = "id_edited")
    private Boolean isEdited;

    @SerializedName("date_edited")
    @Column(name="date_edited")
    private Date dateEdited;

    @SerializedName("is_deleted")
    @Column(name="is_deleted")
    private Boolean isDeleted;

    @SerializedName("date_deleted")
    @Column(name="date_deleted")
    private Date dateDeleted;

    @ManyToOne(optional=false, cascade= CascadeType.ALL)
    @JoinColumn (name="id_dialog")
    private Dialog msgDialog;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setUserFromName(String userFromName) {
        this.userFromName = userFromName;
    }

    public String getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(String userFromId) {
        this.userFromId = userFromId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDateExp() {
        return dateExp;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }

    public Boolean getExpirable() {
        return isExpirable;
    }

    public void setExpirable(Boolean expirable) {
        isExpirable = expirable;
    }

    public Dialog getMsgDialog() {
        return msgDialog;
    }

    public void setMsgDialog(Dialog msgDialog) {
        this.msgDialog = msgDialog;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public Date getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(userFromName, message.userFromName) &&
                Objects.equals(userFromId, message.userFromId) &&
                Objects.equals(value, message.value) &&
                Objects.equals(dateSent, message.dateSent) &&
                Objects.equals(dateExp, message.dateExp) &&
                Objects.equals(isExpirable, message.isExpirable) &&
                Objects.equals(isEdited, message.isEdited) &&
                Objects.equals(dateEdited, message.dateEdited) &&
                Objects.equals(isDeleted, message.isDeleted) &&
                Objects.equals(dateDeleted, message.dateDeleted) &&
                Objects.equals(msgDialog, message.msgDialog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userFromName, userFromId, value, dateSent, dateExp, isExpirable, isEdited, dateEdited, isDeleted, dateDeleted, msgDialog);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", userFromName='" + userFromName + '\'' +
                ", userFromId='" + userFromId + '\'' +
                ", value='" + value + '\'' +
                ", dateSent=" + dateSent +
                ", dateExp=" + dateExp +
                ", isExpirable=" + isExpirable +
                ", isEdited=" + isEdited +
                ", dateEdited=" + dateEdited +
                ", isDeleted=" + isDeleted +
                ", dateDeleted=" + dateDeleted +
                ", msgDialog=" + msgDialog +
                '}';
    }

    public static Message createFromRxMessage(RxMessage rxMessage, Dialog msgDialog) {
        Message message = new Message();
        message.setId(rxMessage.getId());
        message.setUserFromName(rxMessage.getUserFromName());
        message.setUserFromId(rxMessage.getUserFromId());
        message.setValue(rxMessage.getValue());
        message.setDateSent(rxMessage.getDateSent());

        message.setDateExp(rxMessage.getDateExp());
        message.setExpirable(rxMessage.getExpirable());
        message.setMsgDialog(msgDialog);
        message.setDateEdited(rxMessage.getDateEdited());
        message.setDeleted(rxMessage.getDeleted());
        message.setDateDeleted(rxMessage.getDateDeleted());
        return message;
    }
}
