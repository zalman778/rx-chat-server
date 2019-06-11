package com.hwx.rx_chat_server.repository.db_static;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.entity.st.Message;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
//@Transactional("staticTransactionManager")
public interface MessageStaticRepository {//extends CrudRepository<Message, String> {
    Message add(Message message);

    Message get(String id);

    Message update(Message message);

    List<RxMessage> findMessageByDialogId(String dialogId);
}
