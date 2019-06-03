package com.hwx.rx_chat_server.config;


import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(basePackages = "com.hwx.rx_chat_server.repository.rx")
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
        MongoClient mongoClient = MongoClients.create(
                "mongodb://"
                        +environment.getProperty("spring.data.mongodb.host")+":"
                        +environment.getProperty("spring.data.mongodb.port"));
        return mongoClient;
    }
}
