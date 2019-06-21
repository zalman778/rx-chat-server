package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {


    @Autowired
    private MessageCustomRepository messageCustomRepository;

    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, produces = "application/json")
    public List<RxMessage> getMessagesList(@RequestParam String dialogId) {
        return messageCustomRepository.findMessageByDialogId(dialogId);
    }

}
