package com.hwx.rx_chat_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat_server.netty.NettySessionsKeeper;
import com.hwx.rx_chat_server.netty.RSocketObjectController;
import com.hwx.rx_chat_server.netty.RxObjectHandler;
import io.netty.channel.epoll.EpollSocketChannel;
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
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;

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

    @Autowired
    NettySessionsKeeper sessionsKeeper;

    private final Environment environment;

    @Autowired
    public ChatSocketRxController(Environment environment) {
        this.environment = environment;
    }

    private int PORT = 7878;


    private Mono<CloseableChannel> closeable = initRSocket();
    {
        closeable.subscribe(e->{
            logger.info("AVX", e.toString());
            System.out.println(e.toString());
        },e->{
            logger.error("AVX", e);
            System.out.println(e.getMessage());
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
                .port(PORT)
               // .addressSupplier(() -> new InetSocketAddress("127.0.0.1", PORT))
             //   .addressSupplier(() -> new InetSocketAddress("127.0.0.1", PORT))
                .secure(sslContext);

        HttpServer httpServer = HttpServer.from(tcpServer);
        return RSocketFactory
                .receive()

                .acceptor(this::handler)
                .transport(
                       // TcpServerTransport.create("localhost", PORT)
                        WebsocketServerTransport.create(httpServer)
                )
                .start();
    }



    private Mono<RSocket> handler(ConnectionSetupPayload a, RSocket b) {
        InetSocketAddress remoteSocketAddr = null;
        try {
            Class objectClass = b.getClass();
            Field field = objectClass.getDeclaredField("connection");
            field.setAccessible(true);
            Object newObj = field.get(b);

            objectClass = newObj.getClass();
            field = objectClass.getDeclaredField("source");
            field.setAccessible(true);
            newObj = field.get(newObj);

            objectClass = newObj.getClass();
            field = objectClass.getDeclaredField("connection");
            field.setAccessible(true);
            newObj = field.get(newObj);

            objectClass = newObj.getClass().getSuperclass().getSuperclass().getSuperclass();
            field = objectClass.getDeclaredField("connection");
            field.setAccessible(true);
            newObj = field.get(newObj);

            objectClass = newObj.getClass();
            field = objectClass.getDeclaredField("channel");
            field.setAccessible(true);
            newObj = field.get(newObj);
            EpollSocketChannel epollSocketChannel = (EpollSocketChannel) newObj;
            remoteSocketAddr = epollSocketChannel.remoteAddress();

            System.out.println("got rx request from: "+remoteSocketAddr.toString());

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        InetSocketAddress finalRemoteSocketAddr = remoteSocketAddr;
        return Mono.just(new AbstractRSocket() {


            RSocketObjectController rSocketObjectController = new RSocketObjectController(
                    mapper, rxObjectHandler, sessionsKeeper, finalRemoteSocketAddr
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
