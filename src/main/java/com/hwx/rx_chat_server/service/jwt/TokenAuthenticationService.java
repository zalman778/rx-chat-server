package com.hwx.rx_chat_server.service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.response.LoginResponse;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.db_static.UserStaticRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Component
public class TokenAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    @Autowired
    private UserStaticRepository userStaticRepository;

    @Autowired
    ObjectMapper customObjectMapper;

    static final long EXPIRATIONTIME = 864_000_000; // 10 days

    static final String SECRET = "ThisIsASecret";

    static final String TOKEN_PREFIX = "Bearer";

    static final String HEADER_STRING = "Authorization";

    public void addAuthentication(HttpServletResponse res, String username) {
        String JWT = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        //adding bearer token to headers
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);

        //adding LoginResponse to body
        try {
            OutputStream outputStream = res.getOutputStream();
            UserEntity userEntity = userStaticRepository.findByUsername(username);
            LoginResponse loginResponse = LoginResponse.createFromUserEntity(userEntity);
            loginResponse.setStatus("ok");
            loginResponse.setText("ok");
            loginResponse.setToken(TOKEN_PREFIX + " " + JWT);
            String jsonBody = customObjectMapper.writeValueAsString(loginResponse);
            outputStream.write(jsonBody.getBytes());
            outputStream.flush();

        } catch (IOException e) {
            logger.error("error on creating response:", e);
        }
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
                    .getSubject();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));

            return user != null ? new UsernamePasswordAuthenticationToken(user, null, authorities) : null;
        }
        return null;
    }

}
