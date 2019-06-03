package com.hwx.rx_chat_server.repository.db_static.impl;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.object.st.Message;
import com.hwx.rx_chat_server.repository.db_static.MessageStaticRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class MessageStaticRepositoryImpl implements MessageStaticRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Message add(Message message) {
        return entityManager.merge(message);
    }



    @Override
    public Message get(String id) {
        Query query = entityManager.createQuery("select m from Message as m where m.id = :msgId");
        return (Message) query.setParameter("msgId", id).getSingleResult();
    }

    @Override
    public Message update(Message message) {
        return entityManager.merge(message);
    }


    @Override
    public List<RxMessage> findMessageByDialogId(String dialogId) {
        List<RxMessage> messageResponseList = new ArrayList<>();
        Query query = entityManager.createQuery(
                "select m from Message as m join m.msgDialog as d where (m.isDeleted = false or m.isDeleted = null) and d.id = :dialogId");
        query.setParameter("dialogId", dialogId);
//        query.setParameter("bDeleted", true);
        List<Message> messageList = (List<Message>) query.getResultList();
        for (Message message : messageList) {
            messageResponseList.add(RxMessage.createFromStaticMessage(message));
        }
        return messageResponseList;
    }


}
