package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

class HelloHandler {

    private static final String DEFAULT_VALUE = "world";

    private Mono<ServerResponse> getResponse(Optional<String> value) {
        return ServerResponse.ok().body(Mono.just(new HelloResponse(value.orElse(DEFAULT_VALUE))), HelloResponse.class);
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return getResponse(Optional.empty());
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return getResponse(Optional.ofNullable(request.pathVariable("name")));
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).flatMap(
                it -> getResponse(Optional.ofNullable(it.getName()))
        );
    }
}