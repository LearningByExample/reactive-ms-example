package org.learning.by.example.reactive.microservices.handlers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.PathNotFoundException;
import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.learning.by.example.reactive.microservices.test.tags.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@UnitTest
@DisplayName("ErrorHandler Unit Tests")
class ErrorHandlerTest {

    private static final String NOT_FOUND = "not found";

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void notFoundTest() {
        errorHandler.notFound(null)
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    @Test
    void throwableErrorTest() {
        errorHandler.throwableError(new PathNotFoundException(NOT_FOUND))
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    @Test
    void getResponseTest() {
        Mono.just(new PathNotFoundException(NOT_FOUND)).transform(errorHandler::getResponse)
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    private static Consumer<ServerResponse> checkResponse(final HttpStatus httpStatus, final String message) {
        return serverResponse -> {
            assertThat(serverResponse.statusCode(), is(httpStatus));
            assertThat(HandlersHelper.extractEntity(serverResponse, ErrorResponse.class).getError(), is(message));
        };
    }
}
