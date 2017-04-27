package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class HelloHandler {

    private static final String NAME = "name";

    private final ErrorHandler errorHandler;
    private final HelloService helloService;

    private static final Mono<String> DEFAULT_VALUE = Mono.just("world");

    public HelloHandler(final HelloService helloService, final ErrorHandler errorHandler) {
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
                .publish(helloService.getGreetings())
                .publish(buildResponse());
    }

    private Function<Mono<String>, Mono<ServerResponse>> buildResponse() {
        return value -> value.flatMap(name -> ServerResponse.ok()
                .body(Mono.just(new HelloResponse(name)), HelloResponse.class));
    }
}