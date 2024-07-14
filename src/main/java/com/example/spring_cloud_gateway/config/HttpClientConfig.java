package com.example.spring_cloud_gateway.config;

import com.example.spring_cloud_gateway.config.properties.Oauth2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Map;

@Configuration
public class HttpClientConfig {


    @Bean
    @Primary
    public HttpClient defaultClient(){
        return HttpClient.create();
    }
    @Bean
    public HttpClient customClient1(Map<String, Oauth2> oauthProps) {
        return HttpClient.create()
                .headers(entries -> entries.add("X-TEST-HEADER","ZMR-1"));
    }
    @Bean
    public HttpClient customClient2() {
        return HttpClient.create()
                .headers(entries -> entries.add("X-TEST-HEADER","ZMR-2"));
    }

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }


}
