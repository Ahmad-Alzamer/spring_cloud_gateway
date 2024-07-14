package com.example.spring_cloud_gateway.services;

import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RewriteApplicationJson implements RewriteFunction<String, String> {
    @Override
    public Publisher<String> apply(ServerWebExchange serverWebExchange, String s) {
        var gson = new Gson();
        var input = gson.fromJson(s, Map.class);
        input.put("name", input.get("first-name")+" "+input.get("last-name"));
        return Mono.justOrEmpty(gson.toJson(input));
    }
}
