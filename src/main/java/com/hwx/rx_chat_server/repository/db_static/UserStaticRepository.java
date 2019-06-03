package com.hwx.rx_chat_server.repository.db_static;

import com.hwx.rx_chat.common.object.st.Message;
import com.hwx.rx_chat.common.object.st.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserStaticRepository {

    UserEntity findByUsernameAndPasswordHash(String username, String passwordHash);

    UserEntity findByUsername(String username);

    UserEntity add(UserEntity userEntity);
}
