package com.hwx.rx_chat_server.service.st;

import com.hwx.rx_chat.common.request.ProfileInfoUpdateRequest;
import com.hwx.rx_chat.common.request.SignupRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface UserEntityService {
    void signUpUser(SignupRequest signupRequest, String passwordHash);
    void updateUserInfo(ProfileInfoUpdateRequest profileInfoUpdateRequest);
    String updateUserImage(MultipartFile fileData) throws IOException;
}
