package com.hwx.rx_chat_server.repository.db_static;

import com.hwx.rx_chat.common.object.st.Dialog;
import com.hwx.rx_chat.common.response.DialogResponse;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DialogStaticRepository  {
    List<DialogResponse> findLastDialogs(String userId);

    public Dialog loadDialogByDialogId(String dialogId);
}
