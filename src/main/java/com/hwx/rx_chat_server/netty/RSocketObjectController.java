package com.hwx.rx_chat_server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.object.rx.RxObject;
import com.hwx.rx_chat_server.controller.ChatController;
import io.rsocket.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
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
