package com.example.spring_cloud_gateway.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
//public class RequestBodyRewrite implements RewriteFunction<MultiValueMap<String, Part>, MultiValueMap<String, Part>> {
public class RequestBodyRewrite implements RewriteFunction<String, String> {
    @Override
//    public Mono<MultiValueMap<String, Part>> apply(ServerWebExchange exchange, MultiValueMap<String, Part> body) {
    public Mono<String> apply(ServerWebExchange exchange, String body) {
        var gson = new Gson();
        try {
//            var map = gson.fromJson(body, Map.class);
//            map.put("empId", "2345");
//            map.put("department", "Engineering");
//            return Mono.just(gson.toJson(map, Map.class));
            return Mono.just(body);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "An error occured while transforming the request body in class RequestBodyRewrite.",ex);
        }
    }
}