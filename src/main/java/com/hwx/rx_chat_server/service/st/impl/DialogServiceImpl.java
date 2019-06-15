package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.st.DialogStaticRepository;
import com.hwx.rx_chat_server.service.st.DialogService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    private DialogCustomRepository dialogCustomRepository;


    @Autowired
    private DialogStaticRepository dialogStaticRepository;

    @PersistenceContext
    private EntityManager entityManager;


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
            newDialog.setName("dialog of: "+userIdA+ " and "+userIdB);
            UserEntity userEntityA = entityManager.getReference(UserEntity.class, userIdA);
            UserEntity userEntityB = entityManager.getReference(UserEntity.class, userIdB);

            newDialog.getMembers().add(userEntityA);
            newDialog.getMembers().add(userEntityB);
            dialogStaticRepository.save(newDialog);
            return newDialog.getId();
        }
    }
}
