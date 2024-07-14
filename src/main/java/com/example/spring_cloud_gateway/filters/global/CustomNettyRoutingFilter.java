package com.example.spring_cloud_gateway.filters.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CustomNettyRoutingFilter extends NettyRoutingFilter {


    private final Map<String, HttpClient> httpClients;
    public CustomNettyRoutingFilter(HttpClient defaultHttpClient, Map<String, HttpClient> httpClients,
                                    ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider, HttpClientProperties properties) {
        super(defaultHttpClient, headersFiltersProvider, properties);

        this.httpClients = httpClients;
    }

    @Override
    protected HttpClient getHttpClient(Route route, ServerWebExchange exchange) {
        var httpClientName = route.getMetadata().getOrDefault("http-client","defaultClient");
        var httpClient = httpClients.getOrDefault(httpClientName.toString(), super.getHttpClient(route, exchange));
        log.info("inside custom Netty routing filter, will get the http client for [{}], http client: [{}]",httpClientName,httpClient);
        return httpClient;

    }

    @Override
    public int getOrder() {
        return super.getOrder() - 1;
    }
}
