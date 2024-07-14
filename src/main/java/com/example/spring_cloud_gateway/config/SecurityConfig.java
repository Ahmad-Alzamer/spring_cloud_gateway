package com.example.spring_cloud_gateway.config;

import com.example.spring_cloud_gateway.security.CustomSecurityFilter;
import com.example.spring_cloud_gateway.security.RoleHierarchyEnabledReactiveAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, RoleHierarchy roleHierarchy){

        var customFilter = new CustomSecurityFilter();

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .redirectToHttps(httpsRedirectSpec -> httpsRedirectSpec.httpsRedirectWhen(exchange -> false))
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(customFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        //this will use the default AuthorityReactiveAuthorizationManager which -AFAIK- cannot be configured.
//                        .pathMatchers(HttpMethod.GET,"/secured/**").hasAuthority("read")
//                        .pathMatchers(HttpMethod.HEAD,"/secured/**").hasAuthority("read")
//                        .pathMatchers(HttpMethod.POST,"/secured/**").hasAuthority("write")
//                        .pathMatchers(HttpMethod.DELETE,"/secured/**").hasAuthority("write")
//                        .pathMatchers(HttpMethod.PUT,"/secured/**").hasAuthority("write")
//                        .pathMatchers(HttpMethod.PATCH,"/secured/**").hasAuthority("write")
//                        .pathMatchers(HttpMethod.OPTIONS,"/secured/**").hasAuthority("admin")
                        //using the access method with a custom authorization manager since we cannot configure AuthorityReactiveAuthorizationManager to use RoleHierarchy
                        .pathMatchers(HttpMethod.GET,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"read"))
                        .pathMatchers(HttpMethod.HEAD,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"read"))
                        .pathMatchers(HttpMethod.POST,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"write"))
                        .pathMatchers(HttpMethod.DELETE,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"write"))
                        .pathMatchers(HttpMethod.PUT,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"write"))
                        .pathMatchers(HttpMethod.PATCH,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"write"))
                        .pathMatchers(HttpMethod.OPTIONS,"/secured/**").access(RoleHierarchyEnabledReactiveAuthorizationManager.getInstance(roleHierarchy,"admin"))
                        .anyExchange().permitAll()
                )
                .build();
    }
    @Bean
    public MapReactiveUserDetailsService userDetailsService(){
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var users = List.of(
                User.withUsername("read-only")
                        .passwordEncoder(encoder::encode)
                        .password("read-only*")
                        .authorities("read","some-random-role-"+ UUID.randomUUID().toString())
                        .build(),
                User.withUsername("write-only")
                        .passwordEncoder(encoder::encode)
                        .password("write-only*")
                        .authorities("write","some-random-role-"+ UUID.randomUUID().toString())
                        .build(),
                User.withUsername("read-write")
                        .passwordEncoder(encoder::encode)
                        .password("read-write*")
                        .authorities("read","write","some-random-role-"+ UUID.randomUUID().toString())
                        .build(),
                User.withUsername("admin")
                        .passwordEncoder(encoder::encode)
                        .password("admin*")
                        .authorities("admin","some-random-role-"+ UUID.randomUUID().toString())
                        .build(),
                User.withUsername("super-user")
                        .passwordEncoder(encoder::encode)
                        .password("super-user*")
                        .authorities("super-role","some-random-role-"+ UUID.randomUUID().toString())
                        .build()

        );
        return new MapReactiveUserDetailsService(users);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        var map = Map.<String, List<GrantedAuthority>>of(
                "super-role", List.of(new SimpleGrantedAuthority("read"),new SimpleGrantedAuthority("write"),new SimpleGrantedAuthority("admin"))
        );

        return authorities -> Stream.concat(
                        authorities.stream(),
                        authorities.stream().flatMap( authority -> map.getOrDefault( authority.getAuthority(), List.of()).stream())
                )
                .distinct()
                .collect(Collectors.toList());
    }

}


