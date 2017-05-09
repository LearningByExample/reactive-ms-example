package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.exceptions.PathNotFoundException;
import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.learning.by.example.reactive.microservices.handlers.ThrowableTranslator.translate;

public class ErrorHandler {

    private static final String NOT_FOUND = "not found";
    private static final String ERROR_RAISED = "error raised";
    private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    public Mono<ServerResponse> notFound(final ServerRequest request) {
        return Mono.just(new PathNotFoundException(NOT_FOUND)).publish(this::getResponse);
    }

    Mono<ServerResponse> throwableError(final Throwable error) {
        logger.error(ERROR_RAISED, error);
        return Mono.just(error).publish(this::getResponse);
    }

    <T extends Throwable> Mono<ServerResponse> getResponse(Mono<T> monoError) {
        return monoError.publish(translate())
                .flatMap(translation -> ServerResponse
                        .status(translation.getHttpStatus())
                        .body(Mono.just(new ErrorResponse(translation.getMessage())), ErrorResponse.class));
    }
}
