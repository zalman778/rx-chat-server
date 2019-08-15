package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat_server.netty.NettySessionsKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
public class AdminController {

    @Autowired
    private NettySessionsKeeper nettySessionsKeeper;

    @GetMapping("/clean_sessions")
    public String cleanSessions() {
        nettySessionsKeeper.cleanSessions();
        return "OK";
    }
}
