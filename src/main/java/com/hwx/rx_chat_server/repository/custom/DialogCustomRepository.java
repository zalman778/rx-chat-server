package com.hwx.rx_chat_server.repository.custom;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.response.DialogResponse;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DialogCustomRepository {
    List<DialogResponse> findLastDialogs(String userId);

    Dialog loadDialogByDialogId(String dialogId);

    String findExistingDialogByUserIdAAndUserIdB(String userIdA, String userIdB);

}
