package com.hwx.rx_chat_server.repository.st;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserEntityRepository extends CrudRepository<UserEntity, String> {

    public UserEntity findByUsername(String username);

    public UserEntity findByMail(String email);
}
