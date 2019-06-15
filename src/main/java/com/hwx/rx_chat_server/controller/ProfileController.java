package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.request.ProfileInfoUpdateRequest;
import com.hwx.rx_chat.common.request.SignupRequest;
import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.UserDetailsResponse;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.service.st.UserEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    UserEntityService userEntityService;

    @Autowired
    private UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @RequestMapping(value = "/api/signup", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse signUpUser(@RequestBody SignupRequest signupRequest) {
        try {
            userEntityService.signUpUser(signupRequest, bCryptPasswordEncoder.encode(signupRequest.getPassword()));
            return new DefaultResponse("ok", "");

        } catch (Exception e) {
            logger.error("AVX", e);
            return new DefaultResponse("err", "Error on registration");
        }
    }



    @RequestMapping(value = "/api/profile/update_bio", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse updateUserInfo(@RequestBody ProfileInfoUpdateRequest profileInfoUpdateRequest) {
        try {
            userEntityService.updateUserInfo(profileInfoUpdateRequest);
            return new DefaultResponse("ok", "");
        } catch (ConstraintViolationException | RollbackException e) {
            return new DefaultResponse("err", "Duplicate username, please pick another!");
        } catch (Exception e) {
            return new DefaultResponse("err", "undefined error");
        }
    }


    //profile image upload:
    @PostMapping(value = "/api/profile/upload_avatar" )
    public DefaultResponse updateUserImage(
            @RequestPart(name = "img") MultipartFile fileData
    ) {
        try {
            String imageFileName = userEntityService.updateUserImage(fileData);
            return new DefaultResponse("ok", "", "api/userdata/profile/image/"+imageFileName);
        } catch (Exception e) {
            logger.error("AVX", e);
            return new DefaultResponse("err", "Error on saving image...");

        }
    }

    @GetMapping("/api/profile/{id}")
    public UserDetailsResponse getProfileInfo(@PathVariable String id) {
        UserEntity userEntity = userEntityStaticRepository.findById(id).get();
        return new UserDetailsResponse(userEntity.getId()
                , userEntity.getAvatarUrl()
                , userEntity.getFirstName()
                , userEntity.getLastName()
                , userEntity.getUsername()
                , userEntity.getBio()
        );
    }


    @GetMapping("/api/userdata/profile/image/{imageId}")
    public StreamingResponseBody getProfileImageData(
              @PathVariable String imageId
            , HttpServletResponse response
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
