package com.example.spring_cloud_gateway.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class JsonPlaceHolderHealthIndicator implements ReactiveHealthIndicator {
    private final WebClient webClient;
    private final String url ;

    public JsonPlaceHolderHealthIndicator(WebClient webClient, @Value("${apis.json-placeholder.base-url}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    @Override
    public Mono<Health> health() {
        return webClient.head()
                .uri(url + "posts/1")
                .retrieve()
                .toBodilessEntity()
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(response -> Health.up().withDetail("check-time", LocalDateTime.now().toString()))
                .switchIfEmpty(Mono.just(Health.down()))
                .onErrorResume(throwable -> Mono.just(Health.down().withDetail("check-time", LocalDateTime.now().toString()).withDetail("error",throwable.getMessage())))
                .map(Health.Builder::build);
    }
}
