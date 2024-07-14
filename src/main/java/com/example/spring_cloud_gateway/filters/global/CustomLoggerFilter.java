package com.example.spring_cloud_gateway.filters.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
@Slf4j
public class CustomLoggerFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var routeId = Optional.ofNullable(exchange.getAttribute(GATEWAY_ROUTE_ATTR))
                .filter(Route.class::isInstance)
                .map( Route.class::cast)
                .map(Route::getId);
        log.info("received new request, routeId: [{}]",routeId.orElse("could not figure out the route ID"));
        Runnable postRequestLogging = () -> log.info("completed request, routeId: [{}]", routeId.orElse("could not figure out the route ID"));

        return chain.filter(exchange)
                .then(Mono.fromRunnable(postRequestLogging));

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
