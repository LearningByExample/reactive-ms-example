package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

class HelloHandler {

    private ErrorHandler error;

    private static final String DEFAULT_VALUE = "world";

    HelloHandler() {
        error = new ErrorHandler();
    }

    private static BiFunction<HttpStatus, Mono<String>, Mono<ServerResponse>> response =
            (status, value) -> value.flatMap(name -> ServerResponse.status(status).body(Mono.just(
                    new HelloResponse(name)), HelloResponse.class));

    private Function<Mono<String>, Mono<ServerResponse>> getResponse() {
        return (value) -> response.apply(HttpStatus.OK, HelloService.getHello().apply(value).onErrorResume(Mono::error));
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return getResponse().apply(Mono.just(DEFAULT_VALUE)).onErrorResume(error::badRequest);
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return getResponse().apply(Mono.just(request.pathVariable("name"))).onErrorResume(error::badRequest);
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).flatMap(helloRequest -> getResponse().apply(Mono.just(helloRequest.getName())))
                .onErrorResume(error::badRequest);
    }
}