package com.hwx.rx_chat_server.netty;

import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/*
    Хранит мапу реактивных сессий по айди клиента.
 */
@Component
public class NettySessionsKeeper {

    private HashMap<String, SessionObject> objectMap = new HashMap<>();

    public SessionObject getObject(String clientId) {
        if (!objectMap.containsKey(clientId)) {
            SessionObject sessionObject = new SessionObject();
            objectMap.put(clientId, sessionObject);
        }
        return objectMap.get(clientId);
    }

    public SessionObject getSessionObjectByUserId(String userId) {
        for (SessionObject sessionObject : objectMap.values()) {
            if (sessionObject.getUserId() != null && sessionObject.getUserId().equals(userId))
                return sessionObject;
        }
        return null;
    }

    public void createIfNotExists(String clientId, InetSocketAddress remoteSocketAddr) {
        SessionObject sessionObject = getObject(clientId);
        sessionObject.setUserIp(remoteSocketAddr.getAddress().getHostAddress());
        sessionObject.setUserPort(remoteSocketAddr.getPort());
    }

    //очищаем все старые сессии этого клиента
    public void removeSessionsWithUserId(String userId) {
        for (Map.Entry<String, SessionObject> entry : objectMap.entrySet()) {
            if (entry.getValue().getUserId() != null && entry.getValue().getUserId().equals(userId)) {
                objectMap.remove(entry.getKey());
            }
        }
    }
}
