package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.object.st.UserEntity;
import com.hwx.rx_chat.common.request.SignupRequest;
import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.DialogResponse;
import com.hwx.rx_chat_server.repository.db_static.DialogStaticRepository;
import com.hwx.rx_chat_server.repository.db_static.MessageStaticRepository;
import com.hwx.rx_chat_server.repository.db_static.UserStaticRepository;
import com.hwx.rx_chat_server.service.jwt.TokenAuthenticationService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    @Autowired
    private DialogStaticRepository dialogStaticRepository;

    @Autowired
    private MessageStaticRepository messageStaticRepository;

    @Autowired
    private UserStaticRepository userStaticRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //@PostMapping("/api/dialogs")
    @RequestMapping(value = "/api/dialogs", method = RequestMethod.POST, produces = "application/json")
    public List<DialogResponse> getDialogList(@RequestParam String userId) {
        return dialogStaticRepository.findLastDialogs(userId);
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, produces = "application/json")
    public List<RxMessage> getMessagesList(@RequestParam String dialogId) {
        return messageStaticRepository.findMessageByDialogId(dialogId);
    }

    @RequestMapping(value = "/api/signup", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse signUpUser(@RequestBody SignupRequest signupRequest) {
        //TODO: move to service
        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(new ObjectId().toString());
            userEntity.setActive(true);
            userEntity.setName(signupRequest.getUsername());
            userEntity.setMail(signupRequest.getEmail());
            userEntity.setPasswordHash(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
            userStaticRepository.add(userEntity);
            return new DefaultResponse("ok", "");

        } catch (Exception e) {
            logger.error("AVX", e);
            return new DefaultResponse("err", "Error on registration");
        }
    }

}
