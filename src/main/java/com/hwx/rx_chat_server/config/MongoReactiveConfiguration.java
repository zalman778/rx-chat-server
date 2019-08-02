package com.hwx.rx_chat_server.config;


import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@EnableReactiveMongoRepositories(basePackages = "com.hwx.rx_chat_server.rxrepository")
public class MongoReactiveConfiguration extends AbstractReactiveMongoConfiguration {

    private final Environment environment;

    @Autowired
    public MongoReactiveConfiguration(Environment environment) {
        this.environment = environment;
    }


    @Override
    protected String getDatabaseName() {
        return environment.getProperty("spring.data.mongodb.database");
    }

    @Bean
    @Override
    public MongoClient reactiveMongoClient() {
        List<ServerAddress> srvList = new ArrayList<>();
        srvList.add(new ServerAddress(environment.getProperty("spring.data.mongodb.host"), Integer.parseInt(environment.getProperty("spring.data.mongodb.port"))));

        MongoClient mongoClient = MongoClients.create(

                "mongodb://"
                        +environment.getProperty("spring.data.mongodb.host")+":"
                        +environment.getProperty("spring.data.mongodb.port"));
        return mongoClient;
    }
}
