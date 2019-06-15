package com.hwx.rx_chat_server.repository.st;

import com.hwx.rx_chat.common.entity.st.Friendship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface FriendshipStaticRepository extends CrudRepository<Friendship, String> {
}
