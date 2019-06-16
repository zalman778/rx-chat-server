package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.FriendResponse;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FriendsController {
    private static final Logger logger = LoggerFactory.getLogger(FriendsController.class);

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;



    //весь список друзей пользователя
    @RequestMapping(value = "/api/friends", method = RequestMethod.POST, produces = "application/json")
    public List<FriendResponse> getFriendsList(@RequestParam String userId) {
        List<FriendResponse> tempList = userEntityStaticRepository.findById(userId).get().getFriends()
                .stream()
               // .map(Friendship::getRequester)
                .map(e->
                    new FriendResponse(
                          e.getRequester().getId()
                        , e.getRequester().getUsername()
                        , e.getRequester().getAvatarUrl()
                        , e.getAccepted()
                        , e.getId()
                    )
                )
                .collect(Collectors.toList());
        return tempList;
    }



    //запросы в друзья
    @RequestMapping(value = "/api/friends/request/accept", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse acceptFriendRequest(@RequestParam String requestId) {
        try {
            friendshipService.acceptFriendRequest(requestId);

            return new DefaultResponse("ok", "ok");
        } catch (Exception e) {
            logger.error("err", e);
            return new DefaultResponse("err", "err");
        }
    }

    @RequestMapping(value = "/api/friends/request/reject", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse rejectFriendRequest(@RequestParam String requestId) {
        try {
            friendshipService.rejectFriendRequest(requestId);
            return new DefaultResponse("ok", "ok");
        } catch (Exception e) {
            logger.error("err", e);
            return new DefaultResponse("err", "err");
        }

    }

    @GetMapping("/api/friends/request/create/{profileId}")
    public DefaultResponse getProfileInfo(@PathVariable String profileId) {
        try {
            friendshipService.createFriendRequest(profileId);
            return DefaultResponse.OK();
        } catch (Exception e) {
            logger.error("err", e);
            return DefaultResponse.ERR("err");
        }
    }

}
