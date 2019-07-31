package com.hwx.rx_chat_server.rsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.object.rx.RxObject;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.*;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ResourceUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

@Configuration
public class RSocketConfiguration {

    @Autowired
    private Environment environment;

    @Qualifier("customObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;



    ReplayProcessor<RxObject> replyProcessor = ReplayProcessor.create();
    Flux<RxObject> flux = replyProcessor.replay(10).autoConnect();
    FluxSink<RxObject> sink = replyProcessor.sink();


    private int PORT = 7879;

    @Bean
    public Disposable getRSocket(
            TcpServer tcpServer
            ,SocketAcceptor socketAcceptor
    ) {
        HttpServer httpServer = HttpServer.from(tcpServer);
        System.out.println("starting rsocket on port "+PORT);

        Mono<CloseableChannel> closeableChannel = RSocketFactory
                .receive()

                .acceptor(socketAcceptor)
                .transport(
                        // TcpServerTransport.create("localhost", PORT)
                        WebsocketServerTransport.create(httpServer)
                )
                .start();
        return closeableChannel
                .subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Bean
    public TcpServer getTcpServer(SslContext sslContext) {
        return TcpServer.create()
                .addressSupplier(() -> new InetSocketAddress("127.0.0.1", PORT))
                .secure(sslContext);
    }

    @Bean
    public SslContext readSslContext() {
        InputStream certPem = null;
        InputStream keyPem = null;
        SslContext sslContext = null;
        try {

//            certPem = new FileInputStream(ResourceUtils.getFile(environment.getProperty("server.ssl.pem.crt.path")));
//            keyPem = new FileInputStream(ResourceUtils.getFile(environment.getProperty("server.ssl.pem.key.path")));
            certPem = new FileInputStream(ResourceUtils.getFile("classpath:config/server.crt.pem"));
            keyPem = new FileInputStream(ResourceUtils.getFile("classpath:config/server.key.pem"));

            sslContext = SslContextBuilder.forServer(certPem, keyPem)
                    .build();
        } catch (FileNotFoundException | SSLException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    @Bean
    public SocketAcceptor ioRsocketSocketAcceptor() {
        return ((setup, sendingSocket) -> Mono.just(new AbstractRSocket() {
            @Override
            public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                System.out.println("connected new client...");
                Flux
                    .from(payloads)
                        //.map(this::convertToObject)
                        .subscribe(payload->{
                            RxObject rxObject = convertToObject(payload);
                            System.out.println(rxObject);
                        }, err-> {
                            System.out.println("err" + err.getMessage());
                        });
                       // .subscribe(rxObject -> System.out.println(rxObject.toString()));

                return Flux.empty();
            }

            private RxObject convertToObject(Payload payload) {
                try {
                    return objectMapper.readValue(payload.getDataUtf8(), RxObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            private Payload convertToPayload(RxObject rxObject) {
                try {
                    return DefaultPayload.create(objectMapper.writeValueAsBytes(rxObject));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }

        }));


     }

}
