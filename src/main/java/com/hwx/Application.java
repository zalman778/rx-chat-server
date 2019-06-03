package com.hwx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySource(value={
        "classpath:config/application.yml"
        ,"classpath:config/external.properties"
        ,"classpath:config/application.properties"
}, ignoreResourceNotFound = true)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}