package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.Friendship;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.st.FriendshipStaticRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.FriendshipService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Date;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipStaticRepository friendshipStaticRepository;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    private EntityManager entityManager;

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

    @Override
    public void createFriendRequest(String profileId) {
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userFrom = userEntityStaticRepository.findByMail(mail);
        UserEntity userTo = entityManager.getReference(UserEntity.class, profileId);
        Friendship friendship = new Friendship();
        friendship.setId(new ObjectId().toString());
        friendship.setRequester(userFrom);
        friendship.setUser(userTo);
        friendship.setDate(new Date());
        friendship.setActive(true);
        friendship.setAccepted(false);
        friendshipStaticRepository.save(friendship);
    }

}
