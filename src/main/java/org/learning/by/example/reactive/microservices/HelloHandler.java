package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

class HelloHandler {

    private Logger logger = LoggerFactory.getLogger(HelloHandler.class);

    private static final String DEFAULT_VALUE = "world";

    private Mono<ServerResponse> getResponse(String value) {
        if (value.equals("")) {
            return Mono.error(new InvalidParametersException("bad parameters"));
        }
        return ServerResponse.ok().body(Mono.just(new HelloResponse(value)), HelloResponse.class);
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return getResponse(DEFAULT_VALUE);
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return getResponse(request.pathVariable("name"));
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).flatMap(it -> getResponse(it.getName()))
                .doOnError(throwable -> logger.error("error on post hello", throwable))
                .onErrorReturn(InvalidParametersException.class,
                ServerResponse.badRequest().body(Mono.just(
                        new ErrorResponse("bad request")), ErrorResponse.class).block()
                );
    }
}