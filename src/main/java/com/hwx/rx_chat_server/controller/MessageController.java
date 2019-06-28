package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {


    @Autowired
    private MessageCustomRepository messageCustomRepository;

    @RequestMapping(value = "/api/messages/{dialogId}", method = RequestMethod.GET, produces = "application/json")
    public List<RxMessage> getMessagesList(@PathVariable String dialogId) {
        return messageCustomRepository.findMessageByDialogId(dialogId);
    }

}
