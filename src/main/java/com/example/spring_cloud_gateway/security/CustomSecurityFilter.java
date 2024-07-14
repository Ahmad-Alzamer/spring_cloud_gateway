package com.example.spring_cloud_gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMessage;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Optional;

@Slf4j
public class CustomSecurityFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        var s = Optional.of(exchange.getRequest())
                .map(HttpMessage::getHeaders)
                .map(val -> val.get("Authorization"))
                .filter(strings -> !strings.isEmpty())
                .map(strings -> strings.get(0))
                .map(string -> string.split(" "))
                .filter(authorizationHeaderParts -> authorizationHeaderParts.length >= 2)
                .map(authorizationHeaderParts -> authorizationHeaderParts[1])
                .map(string -> Base64.getDecoder().decode(string))
                .map(String::new)
                .orElse("not found");
        log.debug("authorization header: [{}]", s);

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authentication -> log.debug("authentication object: [{}]", authentication))
                .then(chain.filter(exchange));
    }
}
