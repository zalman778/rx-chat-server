package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.st.DialogStaticRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.DialogService;
import com.hwx.rx_chat_server.util.ImageUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    private DialogCustomRepository dialogCustomRepository;


    @Autowired
    private DialogStaticRepository dialogStaticRepository;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Environment environment;

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
            String dialogName = userEntityA.getUsername()+ "; "+userEntityB.getUsername();
            newDialog.setName(dialogName);


            //generating image of dialog:
            String imageFileName = UUID.randomUUID().toString()+".png";
            String uploadRootPath = environment.getProperty("server.context.upload.path");
            File uploadRootDir = new File(uploadRootPath);
            // Create directory if it not exists.
            if (!uploadRootDir.exists()) {
                uploadRootDir.mkdirs();
            }
            String imageText = userEntityA.getUsername().substring(0, 2)+". "+userEntityB.getUsername().substring(0, 2);
            ImageUtil.createImageOfText(imageText,  uploadRootDir.getAbsolutePath() + File.separator + imageFileName, true);
            newDialog.setImageUrl(imageFileName);

            newDialog.getMembers().add(userEntityA);
            newDialog.getMembers().add(userEntityB);
            dialogStaticRepository.save(newDialog);
            return newDialog.getId();
        }
    }

    @Override
    public String createDialog(List<String> pickedProfiles, String dialogCaption) {
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userCreated = userEntityStaticRepository.findByMail(mail);

        Dialog newDialog = new Dialog();
        newDialog.setId(new ObjectId().toString());
        newDialog.setCreateDate(new Date());
        newDialog.setName(dialogCaption);

        //generating short name of dialog:
        String[] captionArr = dialogCaption.split(" ");
        StringBuilder sb = new StringBuilder();
        Arrays.stream(captionArr).forEach(e->sb.append(e, 0, 1));

        String uploadRootPath = environment.getProperty("server.context.upload.path");
        File uploadRootDir = new File(uploadRootPath);
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        String imageFileName = UUID.randomUUID().toString()+".png";
        ImageUtil.createImageOfText(sb.toString(),  uploadRootDir.getAbsolutePath() + File.separator + imageFileName, true);
        newDialog.setImageUrl(imageFileName);


        newDialog.setUserCreated(userCreated);
        newDialog.getMembers().add(userCreated);

        pickedProfiles.forEach(e->{
            UserEntity userEntity = entityManager.getReference(UserEntity.class, e);
            newDialog.getMembers().add(userEntity);
        });

        dialogStaticRepository.save(newDialog);
        return newDialog.getId();

    }

    @Override
    public void deleteDialogMember(String dialogId, String userId) {
        Dialog dialog = dialogStaticRepository.findById(dialogId).get();
        dialog.getMembers().removeIf(userEntity -> userEntity.getId().equals(userId));
        dialogStaticRepository.save(dialog);
    }
}
