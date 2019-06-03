package com.hwx.rx_chat_server.repository.rx;

import com.hwx.rx_chat.common.entity.rx.RxMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReactiveRepository extends ReactiveMongoRepository<RxMessage, String> {

}
