package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class ApiHandler {

    private static final String NAME = "name";

    private final ErrorHandler errorHandler;
    private final HelloService helloService;

    @Autowired
    private QuoteService quoteService;

    private static final Mono<String> DEFAULT_VALUE = Mono.just("world");

    public ApiHandler(final HelloService helloService, final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.helloService = helloService;
    }

    public Mono<ServerResponse> defaultHello(final ServerRequest request) {
        return DEFAULT_VALUE
                .publish(getResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> getHello(final ServerRequest request) {
        return Mono.just(request.pathVariable(NAME))
                .publish(getResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> postHello(final ServerRequest request) {
        return request.bodyToMono(HelloRequest.class)
                .flatMap(helloRequest -> Mono.just(helloRequest.getName()))
                .publish(getResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    private Function<Mono<String>, Mono<ServerResponse>> getResponse() {
        return (value) -> value
                .publish(createResponse())
                .publish(send());
    }

    private Function<Mono<String>, Mono<HelloResponse>> createResponse() {
        return name ->
                name.publish(helloService.getGreetings()).flatMap(
                        greetings -> getQuote().flatMap(
                                title -> Mono.just(new HelloResponse(greetings, title))));
    }

    private Mono<String> getQuote() {
        return quoteService.getQuote().flatMap(quote -> Mono.just(quote.getContent()));
    }

    private Function<Mono<HelloResponse>, Mono<ServerResponse>> send() {
        return value -> value.flatMap(helloResponse -> {
            return ServerResponse.ok().body(Mono.just(helloResponse), HelloResponse.class);
        });
    }
}