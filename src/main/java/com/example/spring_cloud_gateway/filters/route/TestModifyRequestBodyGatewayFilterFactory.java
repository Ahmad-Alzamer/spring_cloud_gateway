package com.example.spring_cloud_gateway.filters.route;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
@Slf4j
public class TestModifyRequestBodyGatewayFilterFactory extends AbstractGatewayFilterFactory<TestModifyRequestBodyGatewayFilterFactory.Config> {
    public TestModifyRequestBodyGatewayFilterFactory() {
        super(TestModifyRequestBodyGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            var gson = new Gson();
            var modifiedRequest = exchange.mutate();
            var x = ServerRequest.create(exchange.mutate().build(), HandlerStrategies.withDefaults().messageReaders());
            x.bodyToMono(new ParameterizedTypeReference<MultiValueMap<String, Part>>(){})
                    .doOnNext(data -> log.info("test data: [{}]",data))
                    .doOnNext(data -> log.info("form: [{}]",data.get("form")))
                    .doOnNext(data -> log.info("attachment: [{}]",data.get("attachment")))
                    .subscribe();
            return chain.filter(modifiedRequest.build());
        });
        //https://stackoverflow.com/a/63614540/8735058
        //https://stackoverflow.com/a/63614393/8735058
        //https://stackoverflow.com/a/64458684/8735058
    }

    public static class Config{

    }
}
