package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.response.FriendResponse;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

    //поиск юзера по юзернейму -
    // TODO - отфильтровать свой ник, отфильтровать уже существующих друзей
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



}
