package com.hwx.rx_chat_server.repository.st;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserEntityStaticRepository extends CrudRepository<UserEntity, String> {
    UserEntity findFirstByUsername(String username);

    List<UserEntity> findAllByUsernameLike(String username);

    UserEntity findByMail(String email);

}
