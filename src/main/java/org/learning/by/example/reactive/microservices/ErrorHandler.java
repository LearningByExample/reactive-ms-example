package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


public class ErrorHandler {

    private Mono<ServerResponse> getResponse(HttpStatus status, String value) {
        return ServerResponse.status(status).body(Mono.just(new ErrorResponse(value)),
                ErrorResponse.class);
    }

    Mono<ServerResponse> notFound(ServerRequest request) {
        return getResponse(HttpStatus.NOT_FOUND, "not found");
    }
}
