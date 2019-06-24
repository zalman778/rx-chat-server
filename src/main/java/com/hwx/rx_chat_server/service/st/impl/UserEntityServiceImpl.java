package com.hwx.rx_chat_server.service.st.impl;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.request.ProfileInfoUpdateRequest;
import com.hwx.rx_chat.common.request.SignupRequest;
import com.hwx.rx_chat_server.repository.custom.UserEntityCustomRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.UserEntityService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class UserEntityServiceImpl implements UserEntityService {

    @Autowired
    private UserEntityCustomRepository userEntityCustomRepository;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    private Environment environment;

    @Override
    public void signUpUser(SignupRequest signupRequest, String passwordHash) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(new ObjectId().toString());
        userEntity.setActive(true);
        userEntity.setUsername(signupRequest.getUsername());
        userEntity.setMail(signupRequest.getEmail());
        userEntity.setRegDate(new Date());
        userEntity.setPasswordHash(passwordHash);
        userEntityCustomRepository.add(userEntity);
    }

    @Override
    public void updateUserInfo(ProfileInfoUpdateRequest profileInfoUpdateRequest) {
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userEntityStaticRepository.findByMail(mail);

        userEntity.setFirstName(profileInfoUpdateRequest.getFirstName());
        userEntity.setLastName(profileInfoUpdateRequest.getLastName());
        userEntity.setUsername(profileInfoUpdateRequest.getUsername());
        userEntity.setBio(profileInfoUpdateRequest.getBio());

        userEntityStaticRepository.save(userEntity);
    }

    @Override
    public String updateUserImage(MultipartFile fileData) throws IOException {
        // Root Directory.
        String uploadRootPath = environment.getProperty("server.context.upload.path");


        File uploadRootDir = new File(uploadRootPath);
        // Create directory if it not exists.
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }

        String fileExt = "jpg";
        int lastDot = fileData.getName().lastIndexOf(".");
        if (lastDot > 0)
            fileExt = fileData.getName().substring(lastDot+1);

        String imageFileName = UUID.randomUUID().toString()+"."+fileExt;

        System.out.println("Client File Name = " + imageFileName);

        // Create the file at server
        File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + imageFileName);

        serverFile.createNewFile();

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(fileData.getBytes());
        stream.close();

        //fethching old info
        String mail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userEntityStaticRepository.findByMail(mail);

        //deleting old one if exists:
        if (userEntity.getAvatarUrl() != null && !userEntity.getAvatarUrl().isEmpty()) {
            String oldFileName = userEntity.getAvatarUrl();
            File oldServerFile = new File(uploadRootDir.getAbsolutePath() + File.separator + oldFileName);

            if (oldServerFile.exists())
                oldServerFile.delete();
        }

        //saving new info:
        userEntity.setAvatarUrl(imageFileName);
        userEntityStaticRepository.save(userEntity);
        return imageFileName;
    }


}
