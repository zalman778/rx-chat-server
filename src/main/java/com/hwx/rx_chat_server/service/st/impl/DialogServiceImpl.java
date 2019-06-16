package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.st.DialogStaticRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.DialogService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    private DialogCustomRepository dialogCustomRepository;


    @Autowired
    private DialogStaticRepository dialogStaticRepository;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;


    @Override
    @Transactional
    public String findOrCreateDialogIdByUserAAndUserB(String userIdA, String userIdB) {
        String dialogId = dialogCustomRepository.findExistingDialogByUserIdAAndUserIdB(userIdA, userIdB);
        if (dialogId != null && dialogId.length() > 0) {
            return dialogId;
        }else {
            Dialog newDialog = new Dialog();
            newDialog.setId(new ObjectId().toString());
            newDialog.setCreateDate(new Date());

            UserEntity userEntityA = userEntityStaticRepository.findById(userIdA).get();
            UserEntity userEntityB = userEntityStaticRepository.findById(userIdB).get();
            newDialog.setName(""+userEntityA.getUsername()+ "; "+userEntityB.getUsername());
            newDialog.getMembers().add(userEntityA);
            newDialog.getMembers().add(userEntityB);
            dialogStaticRepository.save(newDialog);
            return newDialog.getId();
        }
    }
}
