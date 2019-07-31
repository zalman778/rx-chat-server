package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.response.DefaultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class P2PController {

    //returns client ip for p2p connection, only if users are friends!
    @GetMapping(value = "/api/p2p/request_ip/{profileId}")
    public DefaultResponse getClientIpAddr(
              @PathVariable String profileId
            , HttpServletRequest request
    ) {
        return DefaultResponse.OK("ok", request.getRemoteAddr());
    }

}
