package com.hwx.rx_chat_server.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

public class RServerTest {

    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {

//        TcpServer tcpServer = TcpServer.create()
//                .addressSupplier(() -> new InetSocketAddress("127.0.0.1", PORT));
//
//        HttpServer httpServer = HttpServer.from(tcpServer);

        RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new SimpleResponderHandler()))
                .transport(
                        WebsocketServerTransport.create(PORT)
                )
                .start()
                .subscribe();

        System.in.read();


    }

    private static class SimpleResponderHandler extends AbstractRSocket {
        @Override
        public Mono<Payload> requestResponse(Payload payload) {
            System.out.println("payload:" + payload.getDataUtf8());
            return Mono.just(DefaultPayload.create("received" + ":" + payload.getDataUtf8()));
        }

        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {

            Flux.from(payloads)
                    .subscribe(ex-> System.out.println(ex.getDataUtf8()));

            return Flux
                    .fromArray(new String[] {"1", "2", "3"})
                    .delayElements(Duration.ofSeconds(2))
                    .map(DefaultPayload::create);
        }
    }
}
