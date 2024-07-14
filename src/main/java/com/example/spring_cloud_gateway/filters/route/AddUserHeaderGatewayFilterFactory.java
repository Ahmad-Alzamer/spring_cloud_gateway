package com.example.spring_cloud_gateway.filters.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AddUserHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AddUserHeaderGatewayFilterFactory.Config> {
    public AddUserHeaderGatewayFilterFactory() {
        super(AddUserHeaderGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var authenticationMono = ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(Authentication::getPrincipal)
                    .filter(User.class::isInstance)
                    .map(User.class::cast)
                    ;


            return authenticationMono
                    .map(user -> exchange.mutate().request(builder -> builder.headers(httpHeaders -> httpHeaders.add("X-user",user.getUsername()))).build())
                    .flatMap(chain::filter)
                    .switchIfEmpty(chain.filter(exchange));

        };
    }

    @Getter@Setter@NoArgsConstructor
    public static class Config{}
}
