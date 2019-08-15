package com.hwx.rx_chat_server.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwx.rx_chat.common.entity.rx.RxEvent;
import com.hwx.rx_chat.common.entity.rx.RxMessage;
import com.hwx.rx_chat.common.object.rx.RxObject;
import com.hwx.rx_chat.common.object.rx.types.EventType;
import com.hwx.rx_chat.common.object.rx.types.ObjectType;
import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.entity.st.Message;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.util.DateUtil;
import com.hwx.rx_chat_server.netty.misc.WrapperSocketIpAndProfileId;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
import com.hwx.rx_chat_server.repository.st.DialogStaticRepository;
import com.hwx.rx_chat_server.repository.st.UserEntityStaticRepository;
import com.hwx.rx_chat_server.rxrepository.EventReactiveRepository;
import com.hwx.rx_chat_server.rxrepository.MessageReactiveRepository;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/*
    Обработчик рекативных событий, считывает настройки юзернейма, айди диалога, пишет в монго, подписывается на монго
    с учетом текущих настроек.
 */

@Component
public class RxObjectHandler {

    private static final Logger logger = LoggerFactory.getLogger(RxObjectHandler.class);

    @Autowired
    NettySessionsKeeper sessionsKeeper;

    @Autowired
    MessageReactiveRepository messageReactiveRepository;

    @Autowired
    EventReactiveRepository eventReactiveRepository;

    @Autowired
    MessageCustomRepository messageCustomRepository;

    @Autowired
    DialogCustomRepository dialogCustomRepository;

    @Autowired
    DialogStaticRepository dialogStaticRepository;

    @Autowired
    UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    ReactiveMongoOperations reactiveTemplate;

    @Qualifier("customObjectMapper")
    @Autowired
    private ObjectMapper mapper;


    //обработка объектов от клента, сеттим настройки, пишем в базу и все такое
    public void handleObject(RxObject rxObject, String clientId) {
        SessionObject sessionObject = sessionsKeeper.getObject(clientId);
        System.out.println("got rx object:"+rxObject.getObjectType().toString()+" for clientId="+clientId);
        switch (rxObject.getObjectType()) {
            //TODO: warn! запрос айпи для п2п чата, сделать проверку на друг - не друг
            case REQUEST_IP:
                String profileId = (String) rxObject.getValue();
                SessionObject profileObject = sessionsKeeper.getSessionObjectByUserId(profileId);
                if (profileObject != null) {
                    //String userSocketInfo = profileObject.getUserIp()+":"+profileObject.getUserPort();
                    String userSocketInfo = profileObject.getUserIp();
                    System.out.println("sent userSocketInfo: "+userSocketInfo);
                    WrapperSocketIpAndProfileId wrapperSocketIpAndProfileId = new WrapperSocketIpAndProfileId(userSocketInfo, profileId);
                    sessionObject.getUpRequestedUserSocketInfo().onNext(wrapperSocketIpAndProfileId);
                }

            case EVENT:
                switch (rxObject.getEventType()) {
                    case MESSAGE_NEW_FROM_CLIENT: {
                            RxMessage rxMessage = rxObject.getMessage();
                            if (rxMessage.getId() == null)
                                rxMessage.setId(new ObjectId().toString());


                            //adding image URL in rx messages:
                            //TODO: warn implement caching!!!
                            UserEntity userSended = userEntityStaticRepository.findFirstByUsername(rxMessage.getUserFromName());
                            if (userSended != null)
                                rxMessage.setImageUrl(userSended.getAvatarUrl());

                            if (rxMessage.getUserFromName() == null)
                                rxMessage.setUserFromName(sessionObject.getClientUserName());

                            messageReactiveRepository.save(rxMessage).subscribe();

                            Dialog dialog = dialogCustomRepository.loadDialogByDialogId(rxMessage.getIdDialog());
                            messageCustomRepository.add(Message.createFromRxMessage(rxMessage, dialog));
                        }
                        break;

                    //прилетел ивент удаления сообщения - получаем список всех участников диалога, для всех создаем инвет об удалении сообщения
                    case MESSAGE_DELETED: {
                            String messageId = (String) rxObject.getValue();
                            Message message = messageCustomRepository.get(messageId);
                            message.setDeleted(true);
                            message.setDateDeleted(new Date());
                            messageCustomRepository.update(message);

                            messageReactiveRepository.findById(messageId).subscribe(
                                    e -> {
                                        e.setDeleted(true);
                                        e.setDateDeleted(new Date());
                                        messageReactiveRepository.save(e).subscribe(
                                                ex -> logger.info("AVX", "rx updated: " + ex.toString())
                                                , err -> logger.error("AVX", "gor err:" + err.getMessage())
                                        );
                                    });

                            //высылаем уведомления
                            for (UserEntity userEntity : message.getMsgDialog().getMembers()) {

                                //expiring in 1 hr
                                RxEvent rxEvent = new RxEvent(
                                          userEntity.getUsername()
                                        , userEntity.getId()
                                        , EventType.MESSAGE_DELETED
                                        , messageId
                                        ,null
                                        , DateUtil.addNhrsToDate(new Date(), 1)
                                );
                                eventReactiveRepository.save(rxEvent).subscribe();
                            }
                        }
                        break;
                    //прилетел ивент редактирования сообщения - получаем список всех участников диалога, для всех создаем инвет об удалении сообщения
                    case MESSAGE_EDIT:
                        RxMessage rxMessage = rxObject.getMessage();
                        String messageId = rxMessage.getId();
                        Message message = messageCustomRepository.get(messageId);
                        message.setDateEdited(new Date());
                        message.setEdited(true);
                        message.setValue(rxMessage.getValue());
                        messageCustomRepository.update(message);

                        messageReactiveRepository.findById(messageId).subscribe(
                                e -> {
                                    e.setEdited(true);
                                    e.setDateEdited(new Date());
                                    e.setValue(rxMessage.getValue());
                                    messageReactiveRepository.save(e).subscribe(
                                            ex -> logger.info("AVX", "rx updated: " + ex.toString())
                                            , err -> logger.error("AVX", "gor err:" + err.getMessage())
                                    );
                                });

                        //высылаем уведомления
                        for (UserEntity userEntity : message.getMsgDialog().getMembers()) {

                            //expiring in 1 hr
                            RxEvent rxEvent = new RxEvent(
                                      userEntity.getUsername()
                                    , userEntity.getId()
                                    , EventType.MESSAGE_EDIT
                                    , messageId
                                    , message.getValue()
                                    , DateUtil.addNhrsToDate(new Date(), 1)
                            );
                            eventReactiveRepository.save(rxEvent).subscribe();
                        }

                        break;
                }
                break;

            //прилетают настройки от клиента:
            case SETTING:
                switch (rxObject.getSettingType()) {
                    case ID_DIALOG_FOR_CONVERSATION:
                        sessionObject.getUpDialogId().onNext((String)rxObject.getValue());
                       // logger.info("AVX", "got dialogid for client ="+ rxObject.getValue());
                        System.out.println("got dialogid for clientId ="+clientId+"; userId="+ rxObject.getValue());
                        break;
                    case ID_USER_FOR_EVENT:
                        sessionObject.getUpUserIdForEvents().onNext((String)rxObject.getValue());
                        System.out.println("got userIdForEvent forclientId ="+clientId+"; userId="+ rxObject.getValue());
                        break;
                    case ID_USER_FOR_CONVERSATION:
                        sessionObject.getUpUserIdForConversation().onNext((String)rxObject.getValue());
                        System.out.println("got userIdForConversation for clientId ="+clientId+"; userId="+ rxObject.getValue());
                        break;
                    case ID_USER_FOR_BACKGROUND:
                        String userId = (String)rxObject.getValue();
                        sessionsKeeper.removeSessionsWithUserId(userId);
                        sessionObject.getUpUserIdForBackground().onNext(userId);
                        sessionObject.setUserId(userId);
                        System.out.println("got userIdForBackground for clientId ="+clientId+"; userId="+ rxObject.getValue());
                        break;
                }
                break;
        }

    }

    /*
        В зависимости от текущей настройки dialogId и event  - выбираем какая таблица, какой запрос, как флаксить
     */
    public Flux<Payload> getObjectFlux(String clientId) {
        SessionObject sessionObject = sessionsKeeper.getObject(clientId);
        return Flux.merge( //mergeSequential
                  Flux.from(sessionObject.getUpDialogId())
                    .flatMap(e->{
                            logger.info("AVX", "requesting for userId="+e);
                            return reactiveTemplate
                                .changeStream("rxMessage",
                                    ChangeStreamOptions
                                        .builder()
                                        .filter(newAggregation(match(where("operationType").is("insert")
                                                .andOperator(where("idDialog").is(e)
                                                .andOperator(where("isDeleted").is(false)))))
                                        )
                                        .build(),
                                    RxMessage.class)
                                .map(ChangeStreamEvent::getBody)
                                .map(msg-> new RxObject(ObjectType.EVENT, EventType.MESSAGE_NEW_FROM_SERVER, null, msg));
                            }

                    ),
                   ///подписываемся на события по idUser ?!
                   Flux.from(sessionObject.getUpUserIdForEvents())
                        .flatMap(e-> {
                            logger.info("AVX", "got userId="+e);
                            return reactiveTemplate
                                    .changeStream("rxEvent",
                                            ChangeStreamOptions
                                                    .builder()
                                                    .filter(newAggregation(match(where("operationType").is("insert")
                                                        .andOperator(where("userToId").is(e))
                                                    )))
                                                    .build(),
                                            RxEvent.class)
                                    .map(ChangeStreamEvent::getBody)
                                    .map(rxEvent-> new RxObject(ObjectType.EVENT, rxEvent.getEventType(), rxEvent.getValue(), rxEvent.getObjectId()));
                        }

                    )
                    /* подписываемся на запросы айпишников для п2п чата:

                     */
                    ,Flux.from(sessionObject.getUpRequestedUserSocketInfo())
                        .map(socketInfo->
                                new RxObject(ObjectType.EVENT, EventType.FRIEND_SOCKET_INFO,
                                        socketInfo.getSocketIp(),
                                        socketInfo.getProfileId()
                                )
                        )
                    /* подписываемся на все сообщения для юзера
                     * при получении нового юзер айди - получаем из статик бд весь список диалогов, в котором он учавствует:
                     * надо предусмотреть случаи, когда юзера удаляют из чаата или добавляют в него - получаем рхИвенты,
                     * их надо анализировать тут же
                     */

                    ,Flux.from(sessionObject.getUpUserIdForConversation())
                        .flatMap(e->{
                                    logger.info("AVX", "requesting for userId="+e);
                                    List<String> dialogListIds = dialogStaticRepository
                                            .findAllDialogByMembers_Id(e)
                                            .stream().map(userEntity->userEntity.getId())
                                            .collect(Collectors.toList());
                                    logger.info("AVX", "got list of dialogs for user "+dialogListIds.size());
                                    return Flux
                                            .fromIterable(dialogListIds)
                                            .flatMap(dialogId -> reactiveTemplate
                                                    .changeStream("rxMessage",
                                                            ChangeStreamOptions
                                                                    .builder()
                                                                    .filter(newAggregation(match(where("operationType").is("insert")
                                                                            .andOperator(where("idDialog").is(dialogId)
                                                                                    .andOperator(where("isDeleted").is(false)))))
                                                                    )
                                                                    .build(),
                                                            RxMessage.class)
                                                    .map(ChangeStreamEvent::getBody)
                                                    .map(msg-> new RxObject(ObjectType.EVENT, EventType.MESSAGE_NEW_FROM_SERVER, null, msg)));

                                }

                        )
                )
                .map(rxObj ->
                {
                    try {

                        //RxObject rxObject = new RxObject(ObjectType.EVENT, EventType.MESSAGE_NEW_FROM_SERVER, null, rxObj);
                        return DefaultPayload.create(mapper.writeValueAsString(rxObj).getBytes());
                    } catch (JsonProcessingException e1) {
                        e1.printStackTrace();
                    }
                    return null;
                });

    }
}
