package com.example.spring_cloud_gateway.filters.route;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var headersString = exchange.getRequest().getHeaders().entrySet().stream().map(entry -> entry.getKey()+"::"+entry.getValue()).collect(Collectors.joining(" , "));
            log.info("headers: [{}]",headersString);
           return chain.filter(exchange);
        };
    }

    @Data
    @NoArgsConstructor
    public static class Config implements HasRouteId {
        private String routeId;
    }

}
