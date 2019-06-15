package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.Friendship;
import com.hwx.rx_chat_server.repository.st.FriendshipStaticRepository;
import com.hwx.rx_chat_server.service.st.FriendshipService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipStaticRepository friendshipStaticRepository;

    @Override
    public void acceptFriendRequest(String requestId) {
        Friendship friendship = friendshipStaticRepository.findById(requestId).get();
        friendship.setAccepted(true);
        Friendship invertedFriendShipt = new Friendship();
        invertedFriendShipt.setId(new ObjectId().toString());
        invertedFriendShipt.setAccepted(true);
        invertedFriendShipt.setActive(true);
        invertedFriendShipt.setDate(new Date());
        invertedFriendShipt.setRequester(friendship.getUser());
        invertedFriendShipt.setUser(friendship.getRequester());
        friendshipStaticRepository.save(invertedFriendShipt);
        friendshipStaticRepository.save(friendship);
    }

    @Override
    public void rejectFriendRequest(String requestId) {
        Friendship friendship = friendshipStaticRepository.findById(requestId).get();
        friendship.setActive(false);
        friendshipStaticRepository.save(friendship);
    }

}
