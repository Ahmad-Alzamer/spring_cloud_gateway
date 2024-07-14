package com.example.spring_cloud_gateway;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Slf4j
public class FluxExperimentalTests {

    @Test
    public void WILL_EXECUTE_NOTHING_SINCE_NO_SUBSCRIPTION(){
        // Reactive pipelines are lazy execution, unless they are subscribed to they will not execute
        var x = prepareReactivePipeline("test");

    }
    @Test
    public void WILL_EXECUTE_SINCE_SUBSCRIBED_TO_1(){
        // Reactive pipelines are lazy execution, unless they are subscribed to they will not execute
        var x = prepareReactivePipeline("test");
        x.subscribe();// will subscribe and invoke the pipeline but will not get the final value
    }
    @Test
    public void WILL_EXECUTE_SINCE_SUBSCRIBED_TO_2(){
        // Reactive pipelines are lazy execution, unless they are subscribed to they will not execute
        var x = prepareReactivePipeline("test");
        x.subscribe(val-> log.info("final values: [{}]",val));// will subscribe and get the values
    }


    @Test
    public void REACTOR_TESTING_CODE(){
        // Reactive pipelines are lazy execution, unless they are subscribed to they will not execute
        var x = prepareReactivePipeline("test1");
        StepVerifier
                .create(x)
                .expectNext("T","E")// basically, will consume an item from the stream and check if the item matches the input
                .expectNext("S")
                .expectNextMatches(val -> "T".equals(val))// for complex items, you can pass a predicate to perform complex checks
                .expectNextCount(1)// basically, will consume all items from this point of the stream till the success/error signal and check if the count of consumed items matches the input
                .expectComplete()
                .verify();
    }

    private static Flux<String> prepareReactivePipeline(String data) {
        return Mono.just(data)
                .doOnNext(val -> log.info("original value:[{}]",val))
                .map(String::toUpperCase)
                .doOnNext(val -> log.info("upper case value:[{}]",val))
                .flatMapMany(val -> Flux.fromArray(val.split("")))
                .doOnNext(val -> log.info("split value:[{}]",val));
    }

    @Test
    public void test_just_vs_defer() throws InterruptedException {

//        System.out.println(new ParameterizedTypeReference<List<String>>(){});
        System.out.println("not mono time:"+LocalDateTime.now());
//        var time = Mono.just(LocalDateTime.now().toString());
        var time = Mono.just(LocalDateTime.now().toString());
        System.out.println("not mono time:"+LocalDateTime.now());
        System.out.println("time:"+time.block());
        Thread.sleep(2_000);

        System.out.println("time:"+time.block());
        Thread.sleep(2_000);

        System.out.println("time:"+time.block());
        Thread.sleep(2_000);

        time = Mono.defer(()-> Mono.just(LocalDateTime.now().toString()));
        System.out.println("defer time:"+time.block());
        Thread.sleep(2_000);

        System.out.println("defer time:"+time.block());
        Thread.sleep(2_000);

        System.out.println("defer time:"+time.block());
        Thread.sleep(2_000);

    }
    @Test
    public void test_just_vs_defer2() throws InterruptedException {
//        https://stackoverflow.com/a/60093080/8735058
        System.out.println("direct registration to a mono");
        callExternalService("direct")
                .log("unit.test.", Level.WARNING)
                .doOnNext(val -> System.out.println("direct:"+val))
                .repeat(3)
                .subscribe();

        System.out.println("\n\nusing mono.defer");
        Mono.defer(()-> callExternalService("defer"))
                .doOnNext(val -> System.out.println("defer:"+val))
                .repeat(3)
                .subscribe();
    }

    private static Mono<?> callExternalService(String source)
    {
        System.out.println("External service is called from:"+source);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }

        var instant = Instant.now();
        return Mono.just("instant:"+ instant);
    }
}
