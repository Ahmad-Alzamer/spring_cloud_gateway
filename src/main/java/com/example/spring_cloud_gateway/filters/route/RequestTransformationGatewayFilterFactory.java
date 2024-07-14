package com.example.spring_cloud_gateway.filters.route;

import com.example.spring_cloud_gateway.services.RequestBodyRewrite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestTransformationGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestTransformationGatewayFilterFactory.Config> {
    private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilterFactory;
    private final RequestBodyRewrite requestBodyRewrite;

    public RequestTransformationGatewayFilterFactory(ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilterFactory, RequestBodyRewrite requestBodyRewrite) {
        super(RequestTransformationGatewayFilterFactory.Config.class);
        this.modifyRequestBodyFilterFactory = modifyRequestBodyFilterFactory;
        this.requestBodyRewrite = requestBodyRewrite;
    }

    @Override
    public GatewayFilter apply(Config config) {
//https://stackoverflow.com/questions/75789135/how-to-modify-multipart-form-data-request-body-in-spring-cloud-gateway-filter
        //https://github.com/arony/multipart-body-rewrite/blob/main/gateway/src/main/kotlin/com/gateway/ModifyGatewayFilterFactory.kt
//        var func =
//                new ModifyRequestBodyGatewayFilterFactory.Config()
//                        .setRewriteFunction(new ParameterizedTypeReference<MultiValueMap<String, Part>>(){},
//                                new ParameterizedTypeReference<MultiValueMap<String, Part>>(){},
//                                requestBodyRewrite);
//        var func =
//                new ModifyRequestBodyGatewayFilterFactory.Config()
//                        .setRewriteFunction(String.class, String.class,requestBodyRewrite);
//        return (exchange, chain) -> {
//            var newExchange = exchange.mutate().request(exchange.getRequest().mutate().build()).build();
//            var request = ServerRequest.create(newExchange, HandlerStrategies.withDefaults().messageReaders());
////            var request = ServerRequest.create(exchange, List.of( new MultipartHttpMessageReader(new DefaultPartHttpMessageReader())));
//            var modified = request.bodyToMono(new ParameterizedTypeReference<MultiValueMap<String, Part>>() {})
//                    .doOnNext(body -> log.info("body: [{}]",body))
//            ;
//            modified.subscribe();
//            return modifyRequestBodyFilter.apply(func)
//                    .filter(exchange, chain);
//        };


//        var modificationConfig =new ModifyRequestBodyGatewayFilterFactory.Config().setRewriteFunction(
//                String.class,
//                String.class,
//                requestBodyRewrite);
//        return modifyRequestBodyFilterFactory.apply(modificationConfig);



//https://github.com/spring-cloud/spring-cloud-gateway/issues/2696
//
       return ((exchange, chain) -> {
           var serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
           var modifiedBody = serverRequest.bodyToMono(
                   new ParameterizedTypeReference<MultiValueMap<String, Part>>() {
                   }
           ).flatMap(origBody -> {
               log.info("test", origBody);
               return Mono.justOrEmpty(origBody);
           });
           var bodyInserters = BodyInserters.fromPublisher(modifiedBody, new ParameterizedTypeReference<MultiValueMap<String, Part>>() {
           });

           return chain.filter(exchange);
       });


//        return ((exchange, chain) -> {
//            var serverRequest = ServerRequest.create(exchange,HandlerStrategies.withDefaults().messageReaders());
//            Mono<MultiValueMap<String, Part>> modifiedBody = serverRequest.bodyToMono(
//                    new ParameterizedTypeReference<MultiValueMap<String, Part>>() {}
//            ).flatMap(originalBody -> {
//                // check sign and re-sign logic......
//                return Mono.just(originalBody);
//            });
//            var bodyInserter = BodyInserters.fromPublisher(modifiedBody,
//                    new ParameterizedTypeReference<MultiValueMap<String, Part>>() {});
//            var headers = new HttpHeaders();
//            headers.putAll(exchange.getRequest().getHeaders());
//            headers.remove(HttpHeaders.CONTENT_LENGTH);
//            var outputMessage = new CachedBodyOutputMessage(exchange, headers);
//            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
//                var decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
//                    @Override
//                    public HttpHeaders getHeaders() {
//                        long contentLength = headers.getContentLength();
//                        HttpHeaders httpHeaders = new HttpHeaders();
//                        httpHeaders.putAll(headers);
//                        if (contentLength > 0) {
//                            httpHeaders.setContentLength(contentLength);
//                        } else {
//                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
//                        }
//                        return httpHeaders;
//                    }
//
//                    @Override
//                    public Flux<DataBuffer> getBody() {
//                        return outputMessage.getBody();
//                    }
//                };
//                return chain.filter(exchange.mutate().request(decorator).build());
//            }));
//        });
//https://github.com/spring-cloud/spring-cloud-gateway/issues/2696#issuecomment-1702340946
//    return ((exchange, chain) -> {
//        return exchange.getMultipartData().map(multiValueMap -> {
//                    // Here we do data decryption and reconstruct the form data.
//                    var multipartBodyBuilder = new MultipartBodyBuilder();
//                    for (var entry : multiValueMap.entrySet()){
//                        for(var formPart : entry.getValue()) {
//                            var partBuilder = multipartBodyBuilder.part(entry.getKey(), formPart.content());
//                            formPart.headers().forEach((key, value) -> partBuilder.header(key, value.toArray(new String[0])));
//                        }
//                    }
//                    var bodyInserter = BodyInserters.fromMultipartData(multipartBodyBuilder.build());
//                    return bodyInserter;
//                })
//                .flatMap(bodyInserter -> {
//                    var headers = new HttpHeaders();
//                    headers.putAll(exchange.getRequest().getHeaders());
//                    // the new content type will be computed by bodyInserter
//                    // and then set in the request decorator
//                    headers.remove(HttpHeaders.CONTENT_LENGTH);
//                    return bodyInserter.insert(exchange, new BodyInserterContext());
//
//                    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
//                    return bodyInserter.insert(outputMessage, new BodyInserterContext())
//                            .log("modify_request", Level.INFO)
//                            .then(Mono.defer(() -> {
//                                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
//                                return chain.filter(exchange.mutate().request(decorator).build());
//                            })).onErrorResume((Function<Throwable, Mono<Void>>) throwable -> release(exchange,
//                                    outputMessage, throwable));
//
//                });
//
//        });
    }

    //    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return modifyRequestBodyFilter
//                .apply(
//                        new ModifyRequestBodyGatewayFilterFactory.Config()
//                                .setRewriteFunction(String.class, String.class, requestBodyRewrite))
//                .filter(exchange, chain);
//    }
//
    public static class Config{

    }

}