package com.hwx.rx_chat_server.rsocket;

import io.reactivex.processors.PublishProcessor;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

public class RClientTest {

    private static PublishProcessor<Payload> publishProcessor = PublishProcessor.create();

    public static void main(String[] args) throws IOException {




        TcpClient tcpClient = TcpClient.create()
                .host("127.0.0.1")
                .port(RServerTest.PORT);

        HttpClient httpClient = HttpClient.from(tcpClient);

        WebsocketClientTransport websocketClientTransport = WebsocketClientTransport.create(httpClient, "/");

        RSocket rSocketMono = RSocketFactory
                .connect()
                .keepAlive(
                          Duration.ofSeconds(42)
                        , Duration.ofSeconds(60)
                        , 10
                )
                .transport(websocketClientTransport)
                .start()
                .block();

        rSocketMono.requestChannel(publishProcessor)
                .subscribe(ex-> System.out.println("Got response: "+ex.getDataUtf8()));

        Scanner sc = new Scanner(System.in);

        while(true) {
            String value = sc.nextLine();
            publishProcessor.onNext(DefaultPayload.create(value));
        }
    }
}
