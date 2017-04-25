package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

class HelloHandler {

    private ErrorHandler error;

    private static final String DEFAULT_VALUE = "world";

    HelloHandler() {
        error = new ErrorHandler();
    }

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
        return request.bodyToMono(HelloRequest.class).flatMap(helloRequest -> getResponse(helloRequest.getName()))
                .onErrorResume(error::badRequest);
    }
}