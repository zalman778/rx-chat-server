package com.hwx.rx_chat_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat_server.netty.RSocketObjectController;
import com.hwx.rx_chat_server.netty.RxObjectHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.*;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.TopicProcessor;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RSocket endpoint
 */

@Controller
public class ChatSocketRxController {

    private static final Logger logger = LoggerFactory.getLogger(ChatSocketRxController.class);

    @Qualifier("customObjectMapper")
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    RxObjectHandler rxObjectHandler;

    private final Environment environment;

    @Autowired
    public ChatSocketRxController(Environment environment) {
        this.environment = environment;
    }

    private int PORT = 7878;

    private AtomicLong counter = new AtomicLong();
    private TopicProcessor processor = TopicProcessor.create();

    private Mono<CloseableChannel> closeable = initRSocket();
    {
        closeable.subscribe(e->{
            logger.info("AVX", e.toString());
        },e->{
            logger.error("AVX", e);
        });
    }


    private Mono<CloseableChannel> initRSocket() {

        InputStream certPem = null;
        InputStream keyPem = null;
        SslContext sslContext = null;
        try {

            certPem = new FileInputStream(ResourceUtils.getFile("classpath:config/server.crt.pem"));
            keyPem = new FileInputStream(ResourceUtils.getFile("classpath:config/server.key.pem"));

            sslContext = SslContextBuilder.forServer(certPem, keyPem)
                    .build();
        } catch (FileNotFoundException | SSLException e) {
            e.printStackTrace();
        }



        TcpServer tcpServer = TcpServer.create()
                .addressSupplier(() -> new InetSocketAddress("127.0.0.1", PORT))
                .secure(sslContext);

        HttpServer httpServer = HttpServer.from(tcpServer);
        return RSocketFactory
                .receive()

                .acceptor((a, b)-> handler(a, b))
                .transport(
                       // TcpServerTransport.create("localhost", PORT)
                        WebsocketServerTransport.create(httpServer)
                )
                .start();
    }



    private Mono<RSocket> handler(ConnectionSetupPayload a, RSocket b) {
        return Mono.just(new AbstractRSocket() {


            RSocketObjectController rSocketObjectController = new RSocketObjectController(
                    mapper, rxObjectHandler
            );

            //2directional - sending in both ways:
            @Override
            public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                Flux.from(payloads)
                        .subscribe(rSocketObjectController::accept);
                return rSocketObjectController.getReactiveFlux();
            }

        });
    }
}
