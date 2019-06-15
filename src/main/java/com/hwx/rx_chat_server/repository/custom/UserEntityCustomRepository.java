package com.hwx.rx_chat_server.repository.custom;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserEntityCustomRepository {

    UserEntity findByUsernameAndPasswordHash(String username, String passwordHash);

    UserEntity findByUsername(String username);

    UserEntity add(UserEntity userEntity);

}
