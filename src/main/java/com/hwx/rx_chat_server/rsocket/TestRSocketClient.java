//package com.hwx.rx_chat_server.rsocket;
//
//import io.reactivex.processors.PublishProcessor;
//import io.rsocket.Payload;
//import io.rsocket.RSocket;
//import io.rsocket.RSocketFactory;
//import io.rsocket.transport.netty.client.TcpClientTransport;
//import io.rsocket.util.DefaultPayload;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//import java.util.Scanner;
//import java.util.concurrent.CountDownLatch;
//
//public class TestRSocketClient {
//    private static int PORT = 7879;
//
//    public static void main(String[] args) throws InterruptedException {
//
//        PublishProcessor<String> ppValues = PublishProcessor.create();
//
//        Thread th = new Thread(()->{
//            Mono<RSocket> mono =  RSocketFactory.connect()
//                    .transport(TcpClientTransport.create("localhost", 7879))
//                    .start();
//
//            mono.subscribe(rSocket -> rSocket.requestChannel(
//                    Flux
//                            .range(1, 5)
//                            .delayElements(Duration.ofSeconds(2))
//                            .map(obj -> DefaultPayload.create(obj.toString().getBytes()))
//
//
//            )
//            .map(Payload::getDataUtf8)
//            .subscribe(ex-> System.out.println(ex), err-> System.out.println(err.getMessage())), err->err.printStackTrace());
//            System.out.println("stated new channel..");
//        });
//        th.setDaemon(true);
//        th.start();
//
//        Scanner sc = new Scanner(System.in);
//
//        while (true) {
//            String line = sc.nextLine();
//            System.out.println("sending value...");
//            ppValues.onNext(line);
//            System.out.println("");
//        }
//
//
//        //CountDownLatch latch = new CountDownLatch(1);
//        //latch.await();
//    }
//
//}
//
