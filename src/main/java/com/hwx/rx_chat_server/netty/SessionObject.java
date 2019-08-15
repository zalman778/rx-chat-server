package com.hwx.rx_chat_server.netty;

import com.hwx.rx_chat_server.netty.misc.WrapperSocketIpAndProfileId;
import io.reactivex.processors.UnicastProcessor;

/**
 * Session object, for handling session data while rx channel dialog.
 */
public class SessionObject {

    private UnicastProcessor<String> upDialogId = UnicastProcessor.create();

    private UnicastProcessor<String> upUserIdForConversation = UnicastProcessor.create();
    private UnicastProcessor<String> upUserIdForEvents = UnicastProcessor.create();
    private UnicastProcessor<String> upUserIdForBackground = UnicastProcessor.create();

    //запрос на получение айпи, если друг, сделать проверку!!
    private UnicastProcessor<WrapperSocketIpAndProfileId> upRequestedUserSocketInfo = UnicastProcessor.create();

    private UnicastProcessor<String> upUsername = UnicastProcessor.create();
    private String clientUserName; //TODO - switch logic to userId
    private String userId;

    private String userIp;
    private Integer userPort;

    public String getUserId() {
        return userId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public Integer getUserPort() {
        return userPort;
    }

    public void setUserPort(Integer userPort) {
        this.userPort = userPort;
        System.out.println("setter port = "+userPort+" for userID="+userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public UnicastProcessor<String> getUpUsername() {
        return upUsername;
    }

    public UnicastProcessor<String> getUpDialogId() {
        return upDialogId;
    }

    public UnicastProcessor<String> getUpUserIdForConversation() {
        return upUserIdForConversation;
    }

    public UnicastProcessor<String> getUpUserIdForEvents() {
        return upUserIdForEvents;
    }

    public UnicastProcessor<String> getUpUserIdForBackground() {
        return upUserIdForBackground;
    }

    public UnicastProcessor<WrapperSocketIpAndProfileId> getUpRequestedUserSocketInfo() {
        return upRequestedUserSocketInfo;
    }

    @Override
    public String toString() {
        return "SessionObject{" +
                "upDialogId=" + upDialogId +
                '}';
    }
}
