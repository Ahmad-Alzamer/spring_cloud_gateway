package com.example.spring_cloud_gateway.filters.route;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SetDynamicRequestHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<SetDynamicRequestHeaderGatewayFilterFactory.Config> {

    private final Map<String,Mono<String>> suppliers;
    public SetDynamicRequestHeaderGatewayFilterFactory(Map<String, Mono<String>> suppliers) {
        super(Config.class);
        this.suppliers = suppliers;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.debug("getting dynamic header value from supplier : [{}]",config);
            var supplierOptional = Optional.ofNullable(suppliers.get(config.getSupplier()));
            log.debug("supplier [{}] is found : [{}]",config.getSupplier(), supplierOptional.isPresent());

            var modifiedRequest = supplierOptional
                    .map(supplier -> supplier.doOnNext( value -> log.trace("for the header [{}] the value will be set to [{}]",config.getName(), config.getValuePrefix()+value+config.getValuePostfix())))
                    .map(supplier -> supplier.map(headerValue -> exchange.mutate().request(request -> request.headers(headers -> headers.add(config.getName(), config.getValuePrefix() +headerValue+ config.getValuePostfix())).build() ) .build()) )
                    .orElse(Mono.just(exchange));
            return  modifiedRequest.flatMap(chain::filter);
        };
    }

    @Data
    @NoArgsConstructor
    public static class Config implements HasRouteId {
        private String name;
        private String valuePrefix;
        private String valuePostfix;
        private String supplier;
        private String routeId;
    }

}
