package com.hwx.rx_chat_server.repository.custom.impl;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.response.DialogResponse;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class DialogCustomRepositoryImpl implements DialogCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<DialogResponse> findLastDialogs(String userId) {
        Query query = entityManager.createNativeQuery("" +
                "select " +
                "       d.id as \"dialogId\"\n" +
                "       ,d.name \"dialogName\"\n" +
                "      ,(select max(ms.date_sent)\n" +
                "          from message ms\n" +
                "        where ms.id_dialog = d.id) as \"lastDate\"\n" +
                "      ,(select u2.username\n" +
                "         from message ms2\n" +
                "             ,user u2\n" +
                "        where ms2.id_dialog = d.id\n" +
                "          and u2.id = ms2.user_from_id\n" +
                "          and ms2.date_sent = (select max(ms.date_sent)\n" +
                "                               from message ms\n" +
                "                               where ms.id_dialog = d.id)\n" +
                "       ) as \"lastUser\"\n" +
                "     ,(select ms2.value\n" +
                "      from message ms2\n" +
                "      where ms2.id_dialog = d.id\n" +
                "        and ms2.date_sent = (select max(ms.date_sent)\n" +
                "                             from message ms\n" +
                "                             where ms.id_dialog = d.id)\n" +
                "     ) as \"lastMessage\"\n" +
                "     ,d.image_url as \"chatImage\"\n" +
                "  from dialog d\n" +
                "\n" +
                "\n" +
                " where exists (\n" +
                "     select 1\n" +
                "       from dialog_members dm\n" +
                "           ,user u\n" +
                "      where u.id = :userID\n" +
                "         and dm.user_id = u.id\n" +
                "         and dm.dialog_id = d.id\n" +
                "           )" +
                "order by  lastDate desc");
        query.setParameter("userID", userId);
        List<DialogResponse> dialogResponses = new ArrayList<>();
        List<Object[]> res = query.getResultList();
        for (Object[] objArr : res) {
            DialogResponse dialogResponse = new DialogResponse();
            dialogResponse.setDialogId((String)objArr[0]);
            dialogResponse.setDialogName((String)objArr[1]);
            if (objArr[2] != null)
                dialogResponse.setLastDate(
                        new Date(((Timestamp)objArr[2]).getTime())
                );
            dialogResponse.setLastUser((String)objArr[3]);
            dialogResponse.setLastMessage((String)objArr[4]);
            dialogResponse.setChatImage((String)objArr[5]);
            dialogResponses.add(dialogResponse);
        }
        return dialogResponses;
    }

    public Dialog loadDialogByDialogId(String dialogId) {
        Session session = entityManager.unwrap(Session.class);
        return session.load(Dialog.class, dialogId);
    }

    @Override
    public String findExistingDialogByUserIdAAndUserIdB(String userIdA, String userIdB) {
        try {
            Query query = entityManager.createNativeQuery("select d.id\n" +
                    "  from dialog d\n" +
                    "where exists (\n" +
                    "        select 1\n" +
                    "          from dialog_members dm\n" +
                    "        where dm.user_id = :userA\n" +
                    "          and dm.dialog_id = d.id\n" +
                    "          )\n" +
                    "    and exists(\n" +
                    "        select 1\n" +
                    "        from dialog_members dm\n" +
                    "        where dm.user_id = :userB\n" +
                    "          and dm.dialog_id = d.id\n" +
                    "    )\n" +
                    "    and not exists(\n" +
                    "        select 1\n" +
                    "          from dialog_members dm\n" +
                    "       where dm.dialog_id = d.id\n" +
                    "        and dm.user_id not in (:userA, :userB)\n" +
                    "    )");
            query.setParameter("userA", userIdA);
            query.setParameter("userB", userIdB);
            return  (String) query.getResultList().get(0);
        } catch (Exception nre) {
            return null;
        }
    }



}
