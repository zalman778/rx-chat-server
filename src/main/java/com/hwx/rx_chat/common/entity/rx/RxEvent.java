package com.hwx.rx_chat.common.entity.rx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.hwx.rx_chat.common.object.rx.types.EventType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Document
public class RxEvent implements Serializable {

    @SerializedName("user_to")
    @JsonProperty("user_to")
    private String userTo;

    @SerializedName("user_to_id")
    @JsonProperty("user_to_id")
    private String userToId;

    @SerializedName("event_type")
    @JsonProperty("event_type")
    private EventType eventType;

    @SerializedName("object_id")
    @JsonProperty("object_id")
    private String objectId;

    @SerializedName("value")
    @JsonProperty("value")
    private String value;

    @SerializedName("date_expiring")
    @JsonProperty("value")
    @Field
    @Indexed(name="someDateFieldIndex", expireAfterSeconds=7200)
    private Date dateExpiring;

    public RxEvent() {
    }


    public RxEvent(String userTo, String userToId, EventType eventType, String objectId, String value, Date dateExpiring) {
        this.userTo = userTo;
        this.userToId = userToId;
        this.eventType = eventType;
        this.objectId = objectId;
        this.value = value;
        this.dateExpiring = dateExpiring;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getUserToId() {
        return userToId;
    }

    public void setUserToId(String userToId) {
        this.userToId = userToId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public RxEvent(String objectId) {
        this.objectId = objectId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDateExpiring() {
        return dateExpiring;
    }

    public void setDateExpiring(Date dateExpiring) {
        this.dateExpiring = dateExpiring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RxEvent rxEvent = (RxEvent) o;
        return Objects.equals(userTo, rxEvent.userTo) &&
                Objects.equals(userToId, rxEvent.userToId) &&
                eventType == rxEvent.eventType &&
                Objects.equals(objectId, rxEvent.objectId) &&
                Objects.equals(value, rxEvent.value) &&
                Objects.equals(dateExpiring, rxEvent.dateExpiring);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userTo, userToId, eventType, objectId, value, dateExpiring);
    }

    @Override
    public String toString() {
        return "RxEvent{" +
                "userTo='" + userTo + '\'' +
                ", userToId='" + userToId + '\'' +
                ", eventType=" + eventType +
                ", objectId='" + objectId + '\'' +
                ", value='" + value + '\'' +
                ", dateExpiring=" + dateExpiring +
                '}';
    }
}
