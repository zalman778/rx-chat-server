package com.hwx.rx_chat_server.repository.st;

import com.hwx.rx_chat.common.entity.st.Dialog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface DialogStaticRepository extends CrudRepository<Dialog, String> {
    List<Dialog> findAllDialogByMembers_Id(String userId);
}
