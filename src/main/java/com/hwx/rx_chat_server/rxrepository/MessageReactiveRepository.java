package com.hwx.rx_chat_server.rxrepository;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageReactiveRepository extends ReactiveMongoRepository<RxMessage, String> {

}
