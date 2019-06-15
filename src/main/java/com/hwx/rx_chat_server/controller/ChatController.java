package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.DialogResponse;
import com.hwx.rx_chat.common.response.FriendResponse;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.DialogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private DialogService dialogService;

    @Autowired
    private DialogCustomRepository dialogCustomRepository;

    @Autowired
    private MessageCustomRepository messageCustomRepository;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;


    //@PostMapping("/api/dialogs")
    @RequestMapping(value = "/api/dialogs", method = RequestMethod.POST, produces = "application/json")
    public List<DialogResponse> getDialogList(@RequestParam String userId) {
        return dialogCustomRepository.findLastDialogs(userId);
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, produces = "application/json")
    public List<RxMessage> getMessagesList(@RequestParam String dialogId) {
        return messageCustomRepository.findMessageByDialogId(dialogId);
    }

    //поиск юзера по юзернейму -
    // TODO - отфильтровать свой ник
    @RequestMapping(value = "/api/users/search", method = RequestMethod.POST, produces = "application/json")
    public List<FriendResponse> searchUser(@RequestParam String username) {
        List<FriendResponse> tempList = userEntityStaticRepository.findAllByUsernameLike("%"+username+"%")
                .stream()
                .map(e->
                        new FriendResponse(
                                  e.getId()
                                , e.getUsername()
                                , e.getAvatarUrl()
                                , null
                                , null
                        )
                ).collect(Collectors.toList());
        return tempList;
    }

    //получение id диалога (поиск существующего или новый) по айди 2 юзеров:
    @GetMapping(value="/api/dialog/find_or_create/{userA}/{userB}")
    public DefaultResponse findOrCreateDialog(
              @PathVariable String userA
            , @PathVariable String userB
    ) {
        try {
            String dialogID = dialogService.findOrCreateDialogIdByUserAAndUserB(userA, userB);
            return new DefaultResponse("ok", "ok", dialogID);
        } catch (Exception e) {
            return new DefaultResponse("err", "err on processing request");
        }

    }
}
