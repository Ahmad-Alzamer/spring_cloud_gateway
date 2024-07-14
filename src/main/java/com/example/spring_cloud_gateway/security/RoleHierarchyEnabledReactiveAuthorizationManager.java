package com.example.spring_cloud_gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class RoleHierarchyEnabledReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final List<GrantedAuthority> authorities;
    private final RoleHierarchy roleHierarchy;

    private RoleHierarchyEnabledReactiveAuthorizationManager(RoleHierarchy roleHierarchy, List<GrantedAuthority> authorities) {
        this.roleHierarchy = roleHierarchy;
        this.authorities = authorities;
    }

    public static RoleHierarchyEnabledReactiveAuthorizationManager getInstance(RoleHierarchy roleHierarchy, String... authorities) {
        return new RoleHierarchyEnabledReactiveAuthorizationManager(roleHierarchy, AuthorityUtils.createAuthorityList(authorities));
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        log.info("checking authorization for :[{}]", authentication);
        return authentication.filter(Authentication::isAuthenticated)
                .map(Authentication::getAuthorities)
                .flatMapIterable(roleHierarchy::getReachableGrantedAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any((grantedAuthority) -> this.authorities.stream().anyMatch((authority) -> authority.getAuthority().equals(grantedAuthority)))
                .map((granted) -> ((AuthorizationDecision) new AuthorityAuthorizationDecision(granted, this.authorities)))
                .defaultIfEmpty(new AuthorityAuthorizationDecision(false, this.authorities));

    }
}
