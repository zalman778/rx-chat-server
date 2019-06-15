package com.hwx.rx_chat_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hwx.rx_chat_server.exceptions.SecurityConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Configuration
@EnableJpaRepositories("com.hwx.rx_chat_server.repository")
public class SpringConfiguration implements WebMvcConfigurer {

    @Autowired
    private Environment environment;

    @Primary
    @Bean(name = "customObjectMapper")
    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
        objectMapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean(name="serverKeyStore")
    public KeyStore getKeyStore() {
        try {
            String sslKeyStorePath = environment.getProperty("server.ssl.key-store");
            char[] sslKeyStorePassword = environment.getProperty("server.ssl.key-store-password").toCharArray();
            File file = ResourceUtils.getFile(sslKeyStorePath);
            FileInputStream fm = new FileInputStream(file);
            KeyStore ks = KeyStore.getInstance(environment.getProperty("server.ssl.key-store-type"));
            ks.load(fm, sslKeyStorePassword);
            return ks;
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
            throw new SecurityConfigurationException("Cannot extract public key", e);
        }
    }

    @Bean(name="serverPublicKey")
    public PublicKey getCertPublicKey() {
        try {
            String sslKeyStoreAlias = environment.getProperty("server.ssl.key-alias");
            Certificate cert = getKeyStore().getCertificate(sslKeyStoreAlias);
            return cert.getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new SecurityConfigurationException("Cannot extract public key", e);
        }
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter standardConverter =
                new MappingJackson2HttpMessageConverter();
        standardConverter.setPrefixJson(false);
        standardConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN));
        standardConverter.setObjectMapper(createObjectMapper());
        return standardConverter;
    }

}