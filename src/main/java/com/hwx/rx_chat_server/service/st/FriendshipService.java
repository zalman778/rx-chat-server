package com.hwx.rx_chat_server.service.st;

import org.springframework.stereotype.Service;

@Service
public interface FriendshipService {
    void acceptFriendRequest(String requestId);
    void rejectFriendRequest(String requestId);
}
