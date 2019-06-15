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
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.custom.MessageCustomRepository;
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
    UserEntityStaticRepository userEntityStaticRepository;

    @Autowired
    ReactiveMongoOperations reactiveTemplate;

    @Qualifier("customObjectMapper")
    @Autowired
    private ObjectMapper mapper;


    //обработка объектов от клента, сеттим настройки, пишем в базу и все такое
    public void handleObject(RxObject rxObject, String clientId) {
        SessionObject sessionObject = sessionsKeeper.getObject(clientId);
        logger.info("AVX", "got rx object:"+rxObject.getObjectType().toString()+" for clientId="+clientId);
        switch (rxObject.getObjectType()) {
            case EVENT:
                switch (rxObject.getEventType()) {
                    case MESSAGE_NEW_FROM_CLIENT: {
                            RxMessage rxMessage = rxObject.getMessage();
                            if (rxMessage.getId() == null)
                                rxMessage.setId(new ObjectId().toString());


                            //adding image URL in rx messages:
                            //TODO: implement caching!!!
                            UserEntity userSended = userEntityStaticRepository.findFirstByUsername(rxMessage.getUserFromName());
                            if (userSended != null)
                                rxMessage.setImageUrl(userSended.getAvatarUrl());

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
                                RxEvent rxEvent = new RxEvent(userEntity.getUsername()
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
                            RxEvent rxEvent = new RxEvent(userEntity.getUsername()
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
                    case ID_DIALOG:
                        sessionObject.getPrDialogId().onNext((String)rxObject.getValue());
                        logger.info("AVX", "got dialogid for client ="+ rxObject.getValue());
                        break;
                    case USERNAME:
                        sessionObject.getPrUsername().onNext((String)rxObject.getValue());
                        sessionObject.setClientUserName((String)rxObject.getValue());
                        logger.info("AVX", "got username from client ="+sessionObject.getClientUserName());
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
                  Flux.from(sessionObject.getPrDialogId())
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
                   ///подписываемся на события по логину
                   Flux.from(sessionObject.getPrUsername())
                        .flatMap(e-> {
                            logger.info("AVX", "got username"+e);
                            return reactiveTemplate
                                    .changeStream("rxEvent",
                                            ChangeStreamOptions
                                                    .builder()
                                                    .filter(newAggregation(match(where("operationType").is("insert")
                                                        .andOperator(where("userTo").is(e))
                                                    )))
                                                    .build(),
                                            RxEvent.class)
                                    .map(ChangeStreamEvent::getBody)
                                    .map(rxEvent-> new RxObject(ObjectType.EVENT, rxEvent.getEventType(), rxEvent.getValue(), rxEvent.getObjectId()));
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
