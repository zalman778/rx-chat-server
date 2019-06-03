package com.hwx.rx_chat_server.netty;

import io.reactivex.processors.UnicastProcessor;

/**
 * Session object, for handling session data while rx channel dialog.
 */
public class SessionObject {

    private UnicastProcessor<String> prDialogId = UnicastProcessor.create();
    private UnicastProcessor<String> prUsername = UnicastProcessor.create();
    private String clientUserName;

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public UnicastProcessor<String> getPrUsername() {
        return prUsername;
    }

    public void setPrUsername(UnicastProcessor<String> prUsername) {
        this.prUsername = prUsername;
    }

    public UnicastProcessor<String> getPrDialogId() {
        return prDialogId;
    }

    public void setPrDialogId(UnicastProcessor<String> prDialogId) {
        this.prDialogId = prDialogId;
    }

    @Override
    public String toString() {
        return "SessionObject{" +
                "prDialogId=" + prDialogId +
                '}';
    }
}
