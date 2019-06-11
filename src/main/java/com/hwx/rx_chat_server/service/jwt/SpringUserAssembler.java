package com.hwx.rx_chat_server.service.jwt;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;


@Service("springUserAssembler")
public class SpringUserAssembler {

    @Transactional(readOnly = true)
    public User buildUserFromUserEntity(UserEntity userEntity) {
        String username = userEntity.getMail();
        String password = userEntity.getPasswordHash();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        for (SecurityRoleEntity role : userEntity.getRoles()) {
//            authorities.add(new GrantedAuthorityImpl(role.getRoleName()));
//        }
        authorities.add(new SimpleGrantedAuthority("USER"));

        User user = new User(username, password, authorities);
        return user;
    }
}
