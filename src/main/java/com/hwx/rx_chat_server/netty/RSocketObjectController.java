package com.hwx.rx_chat_server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.object.rx.RxObject;
import io.rsocket.Payload;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

/*
    Реактивный обработчик р сокетов. На каждый новый канал генерит ид клиента.
    Делегирует обработку RxObjectHandler.
 */
public class RSocketObjectController implements Consumer<Payload> {


    private String clientId = UUID.randomUUID().toString();

    private ObjectMapper mapper;

    private RxObjectHandler rxObjectHandler;

    private InetSocketAddress remoteSocketAddr;

    private NettySessionsKeeper sessionsKeeper;

    public RSocketObjectController(
            ObjectMapper mapper
            , RxObjectHandler rxObjectHandler
            , NettySessionsKeeper sessionsKeeper
            , InetSocketAddress finalRemoteSocketAddr
    ) {
        this.mapper = mapper;
        this.rxObjectHandler = rxObjectHandler;
        this.remoteSocketAddr = finalRemoteSocketAddr;
        this.sessionsKeeper = sessionsKeeper;

        System.out.println("created new ObjController for clientId "+clientId+"; "+finalRemoteSocketAddr.getAddress().toString()
                +":"+finalRemoteSocketAddr.getPort());
    }


    @Override
    public void accept(Payload payload) {
        try {
            RxObject rxObject = mapper.readValue(payload.getDataUtf8(), RxObject.class);
            System.out.println("accepted rxObj="+rxObject.toString());
            sessionsKeeper.createIfNotExists(clientId, remoteSocketAddr);
            rxObjectHandler.handleObject(rxObject, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Flux<Payload> getReactiveFlux() {
        return rxObjectHandler.getObjectFlux(clientId);

    }
}
