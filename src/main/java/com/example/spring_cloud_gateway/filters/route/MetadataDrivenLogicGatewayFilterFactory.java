package com.example.spring_cloud_gateway.filters.route;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
@Slf4j
public class MetadataDrivenLogicGatewayFilterFactory extends AbstractGatewayFilterFactory<MetadataDrivenLogicGatewayFilterFactory.Config> {

    public MetadataDrivenLogicGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var httpClientName = Optional.ofNullable(exchange.getAttribute(GATEWAY_ROUTE_ATTR))
                    .filter(Route.class::isInstance)
                    .map( Route.class::cast)
                    .map(Route::getMetadata)
                    .map(routeMetadata -> routeMetadata.get("http-client"))
                    .filter( String.class::isInstance )
                    .map( String.class::cast)
                    .orElse("defaultClient");

            log.info("for route: [{}], httpClientName: [{}]",config.getRouteId(),httpClientName);

            return chain.filter(exchange);
        };
    }

    @Data
    @NoArgsConstructor
    public static class Config implements HasRouteId {
        private String routeId;
    }

}
