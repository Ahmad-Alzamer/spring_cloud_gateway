package com.example.spring_cloud_gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

/**
 * a simple POC to show that we can reactively fetch the JWT access token needed for the downstream API
 * and attach it to the request as a header (actual implementation will need to pass in the needed info for the OAuth2 server).
 *
 * ideally, there should be some caching mechanism to cache the AccessTokens for the token's lifetime and only fetch a
 * token when the previous on expires
 */
@Configuration
@Slf4j
public class JwtAccessTokensConfig {
    @Bean
    public Mono<String> randomUserJwtAccessTokens(HttpClient httpClient){
        return httpClient.get()
                .uri("https://www.randomnumberapi.com/api/v1.0/uuid?count=1")
                .responseContent()
                .aggregate()
                .as(content -> content.map(_content -> _content.toString(StandardCharsets.UTF_8)))
                .map(JwtAccessTokensConfig::parseApiKeyResponse)
                .map(apiKeys -> "random-user-api-key-"+apiKeys[0])
                .doOnNext(string -> log.trace("received api key :[{}]",string));
    }



    @Bean
    public Mono<String> jsonPlaceholderJwtAccessTokens(HttpClient httpClient){
        return httpClient.get()
                .uri("https://www.randomnumberapi.com/api/v1.0/uuid?count=1")
                .responseSingle((response, content) -> {
                    if(HttpResponseStatus.OK.equals(response.status())){
                        return content ;
                    }else{
                        throw new RuntimeException("HTTP call failed: ["+response.status()+"]");
                    }
                })
                .map(content -> content.toString(StandardCharsets.UTF_8))
                .map(JwtAccessTokensConfig::parseApiKeyResponse)
                .map(apiKeys -> "json-placeholder-api-key-"+apiKeys[0])
                .doOnNext(string -> log.trace("received api key :[{}]",string));
    }


    @Bean
    // trying with WebClient since I was able to find more documentation on WebClient.
    public Mono<String> jsonPlaceholderJwtAccessTokens2(WebClient webClient){
        return webClient.get()
                .uri("https://www.randomnumberapi.com/api/v1.0/uuid?count=1")
//                .retrieve()
//                .bodyToMono(String.class)
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()){
                        return clientResponse.bodyToMono(String.class);
                    }else{
                        throw new RuntimeException("invalid status: "+clientResponse.statusCode());
                    }
                })
//                .onErrorResume(throwable -> Mono.just("[\"test\"]"))
                .map(JwtAccessTokensConfig::parseApiKeyResponse)
                .map(apiKeys -> "json-placeholder-api-key-"+apiKeys[0])
                .doOnNext(string -> log.trace("received api key :[{}]",string));
    }


    private static String[] parseApiKeyResponse(String _content) {
        try {
            return new ObjectMapper().readValue(_content, String[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
