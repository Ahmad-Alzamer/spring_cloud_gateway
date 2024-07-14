package com.example.spring_cloud_gateway.config;

import com.example.spring_cloud_gateway.config.properties.Oauth2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApisConfig {
    @Bean
    @ConfigurationProperties(prefix = "apis.random-user.oauth2")
    public Oauth2 randomUserOauth2(){
        return new Oauth2();
    }

    @Bean
    @ConfigurationProperties(prefix = "apis.json-placeholder.oauth2")
    public Oauth2 jsonPlaceHolderOauth2(){
        return new Oauth2();
    }
}
