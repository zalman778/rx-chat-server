package com.hwx.rx_chat_server.netty;

import org.springframework.stereotype.Component;

import java.util.HashMap;

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
}
