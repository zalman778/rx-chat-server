package com.hwx.rx_chat_server.repository.rx;

import com.hwx.rx_chat.common.entity.rx.RxEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EventReactiveRepository  extends ReactiveMongoRepository<RxEvent, String> {

}
