package com.hwx.rx_chat_server.netty.misc;

public class WrapperSocketIpAndProfileId {
    private String socketIp;
    private String profileId;

    public WrapperSocketIpAndProfileId(String socketIp, String profileId) {
        this.socketIp = socketIp;
        this.profileId = profileId;
    }

    public String getSocketIp() {
        return socketIp;
    }

    public void setSocketIp(String socketIp) {
        this.socketIp = socketIp;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
