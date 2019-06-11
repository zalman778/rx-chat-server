package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.request.ProfileInfoUpdateRequest;
import com.hwx.rx_chat.common.request.SignupRequest;
import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.DialogResponse;
import com.hwx.rx_chat_server.repository.db_static.DialogStaticRepository;
import com.hwx.rx_chat_server.repository.db_static.MessageStaticRepository;
import com.hwx.rx_chat_server.repository.db_static.UserStaticRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityRepository;
import com.hwx.rx_chat_server.service.jwt.TokenAuthenticationService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.util.List;
import java.util.UUID;

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
    private UserEntityRepository userEntityRepository;

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
            userEntity.setUsername(signupRequest.getUsername());
            userEntity.setMail(signupRequest.getEmail());
            userEntity.setPasswordHash(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
            userStaticRepository.add(userEntity);
            return new DefaultResponse("ok", "");

        } catch (Exception e) {
            logger.error("AVX", e);
            return new DefaultResponse("err", "Error on registration");
        }
    }

    @RequestMapping(value = "/api/profile/update_bio", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse signUpUser(@RequestBody ProfileInfoUpdateRequest profileInfoUpdateRequest) {
        try {
            String mail = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userEntityRepository.findByMail(mail);

            userEntity.setFirstName(profileInfoUpdateRequest.getFirstName());
            userEntity.setLastName(profileInfoUpdateRequest.getLastName());
            userEntity.setUsername(profileInfoUpdateRequest.getUsername());
            userEntity.setBio(profileInfoUpdateRequest.getBio());

            userEntityRepository.save(userEntity);
            return new DefaultResponse("ok", "");
        } catch (ConstraintViolationException | RollbackException e) {
            return new DefaultResponse("err", "Duplicate username, please pick another!");
        } catch (Exception e) {
            return new DefaultResponse("err", "undefined error");
        }
    }


    //profile image upload:
    @PostMapping(value = "/api/profile/upload_avatar" )
    public DefaultResponse updateUserPhoto(
              @RequestPart(name = "img") MultipartFile fileData
            , HttpServletRequest request
    ) {
        // Root Directory.
//        String uploadRootPath = request.getServletContext().getRealPath("upload");
        String uploadRootPath = "/home/hiwoo/projects/git/rx-chat/context-path/upload";



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


        try {
            // Create the file at server
            File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + imageFileName);

            serverFile.createNewFile();

            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(fileData.getBytes());
            stream.close();

            //fethching old info
            String mail = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userEntityRepository.findByMail(mail);

            //deleting old one if exists:
            if (!userEntity.getAvatarUrl().isEmpty()) {
                String oldFileName = userEntity.getAvatarUrl().replace("api/userdata/profile/image/", "");
                File oldServerFile = new File(uploadRootDir.getAbsolutePath() + File.separator + oldFileName);

                if (oldServerFile.exists())
                    oldServerFile.delete();
            }

            //saving new info:
            userEntity.setAvatarUrl("api/userdata/profile/image/"+imageFileName);
            userEntityRepository.save(userEntity);


            return new DefaultResponse("ok", "", "api/userdata/profile/image/"+imageFileName);
        } catch (Exception e) {
            logger.error("AVX", e);
            return new DefaultResponse("err", "Error Write file: " + imageFileName);

        }
    }


    @GetMapping("/api/userdata/profile/image/{imageId}")
    public StreamingResponseBody getProfileImageData(
              @PathVariable String imageId
            , HttpServletResponse response
            , HttpServletRequest request
    ) {
        try {

//          String uploadRootPath = request.getServletContext().getRealPath("upload");
            String uploadRootPath = "/home/hiwoo/projects/git/rx-chat/context-path/upload";
            File uploadRootDir = new File(uploadRootPath);

            File picFile = new File(uploadRootDir.getAbsolutePath() + File.separator + imageId);


            if (picFile.exists()) {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + imageId + "\"");

                InputStream inputStream = new FileInputStream(picFile);
                return outputStream -> {
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        //System.out.println("Writing some bytes of file...");
                        outputStream.write(data, 0, nRead);
                    }
                };
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }



}
