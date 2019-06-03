package com.hwx.rx_chat_server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.object.rx.RxObject;
import io.rsocket.Payload;
import reactor.core.publisher.Flux;

import java.io.IOException;
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

    public RSocketObjectController(
              ObjectMapper mapper
            , RxObjectHandler rxObjectHandler)
    {
        this.mapper = mapper;
        this.rxObjectHandler = rxObjectHandler;
    }


    @Override
    public void accept(Payload payload) {
        try {
            RxObject rxObject = mapper.readValue(payload.getDataUtf8(), RxObject.class);
            rxObjectHandler.handleObject(rxObject, clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Flux<Payload> getReactiveFlux() {
        return rxObjectHandler.getObjectFlux(clientId);

    }
}
