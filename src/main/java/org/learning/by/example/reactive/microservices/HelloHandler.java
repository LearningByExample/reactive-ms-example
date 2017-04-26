package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@Component
class HelloHandler {

    private final ErrorHandler errorHandler;
    private final HelloService helloService;

    private static final Mono<String> DEFAULT_VALUE = Mono.just("world");

    HelloHandler(HelloService helloService, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.helloService = helloService;
    }

    private static BiFunction<HttpStatus, Mono<String>, Mono<ServerResponse>> response =
            (status, value) -> value.flatMap(name -> ServerResponse.status(status).body(Mono.just(
                    new HelloResponse(name)), HelloResponse.class));

    private Function<Mono<String>, Mono<ServerResponse>> getResponse() {
        return (value) -> response.apply(HttpStatus.OK, helloService.getGreetings().apply(value).onErrorResume(Mono::error));
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return getResponse().apply(DEFAULT_VALUE).onErrorResume(errorHandler::throwableError);
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return getResponse().apply(Mono.just(request.pathVariable("name"))).onErrorResume(errorHandler::throwableError);
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).flatMap(helloRequest -> getResponse().apply(Mono.just(helloRequest.getName())))
                .onErrorResume(errorHandler::throwableError);
    }
}