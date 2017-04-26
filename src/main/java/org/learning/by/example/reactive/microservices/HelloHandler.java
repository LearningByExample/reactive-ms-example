package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
class HelloHandler {

    private static final String NAME = "name";

    private final ErrorHandler errorHandler;
    private final HelloService helloService;

    private static final Mono<String> DEFAULT_VALUE = Mono.just("world");

    HelloHandler(HelloService helloService, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.helloService = helloService;
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return DEFAULT_VALUE
                .publish(getResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return Mono.just(request.pathVariable(NAME))
                .publish(getResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
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