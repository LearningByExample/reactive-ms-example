package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.exceptions.PathNotFoundException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.function.Function;

class ThrowableTranslator {
    private final HttpStatus httpStatus;
    private final String message;

    private ThrowableTranslator(Throwable throwable) {
        this.httpStatus = getStatus(throwable);
        this.message = throwable.getMessage();
    }

    private HttpStatus getStatus(final Throwable error) {
        if (error instanceof InvalidParametersException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof PathNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }

    String getMessage() {
        return message;
    }

    static <T extends Throwable> Function<Mono<T>, Mono<ThrowableTranslator>> translate() {
        return throwable -> throwable.flatMap(error -> Mono.just(new ThrowableTranslator(error)));
    }
}
