package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
class ErrorHandler {

    private static final String NOT_FOUND = "not found";
    private static final String ERROR_RAISED = "error raised";
    private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    Mono<ServerResponse> notFound(ServerRequest request) {
        return Mono.just(new PathNotFoundException(NOT_FOUND)).publish(getResponse());}

    Mono<ServerResponse> throwableError(Throwable error) {
        logger.error(ERROR_RAISED, error);
        return Mono.just(error).publish(getResponse());
    }

    private <T extends Throwable> Function<Mono<T>, Mono<ServerResponse>> getResponse() {
        return (error) -> error.flatMap(throwable ->
            ServerResponse
                .status(getStatus(throwable))
                .body(Mono.just(new ErrorResponse(throwable.getMessage())), ErrorResponse.class));
    }

    private HttpStatus getStatus(Throwable error) {
        if (error.getClass().equals(InvalidParametersException.class)) {
            return HttpStatus.BAD_REQUEST;
        } else if (error.getClass().equals(PathNotFoundException.class)) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
