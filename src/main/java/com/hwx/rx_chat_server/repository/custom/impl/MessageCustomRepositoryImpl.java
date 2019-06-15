package com.hwx.rx_chat_server.repository.custom.impl;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.entity.st.Message;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MessageCustomRepositoryImpl implements MessageCustomRepository {

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

    public List<RxMessage> findMessageByDialogId(String dialogId) {
        Query query = entityManager.createNativeQuery(
                "select m.*\n" +
                "    ,u.avatar_url\n" +
                "  from message m\n" +
                "      ,dialog d\n" +
                "      ,user   u\n" +
                " where (m.is_deleted = false or m.is_deleted = null)\n" +
                "   and d.id = :dialogId\n" +
                "   and m.id_dialog = d.id" +
                "   and u.id = m.user_from_id\n" +
                "order by m.date_sent");
        query.setParameter("dialogId", dialogId);

        List<RxMessage> messageResponseList = new ArrayList<>();

        //to map transform
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String,Object>> resultList = query.getResultList();

        for (Map<String, Object> row  : resultList) {
            RxMessage rxMessage = new RxMessage();
            rxMessage.setId((String)row.get("id"));
            rxMessage.setDateExp((Date)row.get("date_exp"));
            rxMessage.setDateSent((Date)row.get("date_sent"));
            rxMessage.setExpirable((Boolean)row.get("is_expirable"));
            rxMessage.setValue((String)row.get("value"));
            rxMessage.setIdDialog((String)row.get("id_dialog"));
            rxMessage.setDateDeleted((Date)row.get("date_deleted"));
            rxMessage.setDateEdited((Date)row.get("date_edited"));
            rxMessage.setDeleted((Boolean)row.get("is_deleted"));
            rxMessage.setEdited((Boolean)row.get("id_edited"));
            rxMessage.setUserFromId((String)row.get("user_from_id"));
            rxMessage.setUserFromName((String)row.get("user_from_name"));
            rxMessage.setImageUrl((String)row.get("avatar_url"));

            messageResponseList.add(rxMessage);
        }
        return messageResponseList;
    }




}
