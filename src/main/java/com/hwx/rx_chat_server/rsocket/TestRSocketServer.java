//package com.hwx.rx_chat_server.rsocket;
//
//import io.reactivex.processors.PublishProcessor;
//import io.rsocket.AbstractRSocket;
//import io.rsocket.Payload;
//import io.rsocket.RSocketFactory;
//import io.rsocket.transport.netty.server.CloseableChannel;
//import io.rsocket.transport.netty.server.WebsocketServerTransport;
//import io.rsocket.util.DefaultPayload;
//import org.reactivestreams.Publisher;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.server.HttpServer;
//import reactor.netty.tcp.TcpServer;
//
//import java.net.InetSocketAddress;
//import java.time.Duration;
//import java.util.concurrent.CountDownLatch;
//
//public class TestRSocketServer {
//    private static int PORT = 7879;
//
//
//
//    public static void main(String[] args) throws InterruptedException {
//
//        Thread th = new Thread(() -> {
//            System.out.println("starting new server...");
//
//            TcpServer tcpServer = TcpServer.create()
//                    .addressSupplier(() -> new InetSocketAddress("localhost", PORT));
//
//            HttpServer httpServer = HttpServer.from(tcpServer);
//
//            PublishProcessor<String> ppValues = PublishProcessor.create();
//
//            Mono<CloseableChannel> closeableChannelMono = RSocketFactory
//                    .receive()
//
//                    .acceptor((setup, sendingSocket) -> Mono.just(new AbstractRSocket() {
//                        @Override
//                        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
//                            System.out.println("connected new client...");
//                            Flux
//                                    .from(payloads)
//                                    //.map(this::convertToObject)
//                                    .subscribe(payload->{
//                                        //RxObject rxObject = convertToObject(payload);
//                                        System.out.println(payload.getDataUtf8());
//                                    }, err-> {
//                                        System.out.println("err" + err.getMessage());
//                                    });
//                            // .subscribe(rxObject -> System.out.println(rxObject.toString()));
//
//                            return Flux
//                                    .range(1, 5)
//                                    .delayElements(Duration.ofSeconds(2))
//                                    .map(obj -> DefaultPayload.create(obj.toString().getBytes()));
//                        }
//
//
//                    }))
//                    .transport(
//                            // TcpServerTransport.create("localhost", PORT)
//                            WebsocketServerTransport.create(httpServer)
//                    )
//                    .start();
//
//            closeableChannelMono.subscribe(obj-> System.out.println(obj), err->err.printStackTrace());
//        });
//        th.setDaemon(true);
//        th.start();
//
//        CountDownLatch latch = new CountDownLatch(1);
//        latch.await();
//    }
//}
