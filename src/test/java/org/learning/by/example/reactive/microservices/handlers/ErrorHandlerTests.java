package org.learning.by.example.reactive.microservices.handlers;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.exceptions.GetQuoteException;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.exceptions.PathNotFoundException;
import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Category(UnitTest.class)
public class ErrorHandlerTests {
    private static final String NOT_FOUND = "not found";

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    public void notFoundTest() {
        errorHandler.notFound(null)
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void throwableErrorTest() {
        errorHandler.throwableError(new PathNotFoundException(NOT_FOUND))
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    @Test
    public void getResponseTest() {
        Mono.just(new PathNotFoundException(NOT_FOUND)).publish(errorHandler.getResponse())
                .subscribe(checkResponse(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    public static Consumer<ServerResponse> checkResponse(final HttpStatus httpStatus, final String message) {
        return serverResponse -> {
            assertThat(serverResponse.statusCode(), is(httpStatus));
            assertThat(HandlersHelper.extractEntity(serverResponse, ErrorResponse.class).getError(), is(message));
        };
    }

    @Test
    public void getStatusTest() {
        assertThat(errorHandler.getStatus(new PathNotFoundException(NOT_FOUND)), is(HttpStatus.NOT_FOUND));
        assertThat(errorHandler.getStatus(new InvalidParametersException(NOT_FOUND)), is(HttpStatus.BAD_REQUEST));
        assertThat(errorHandler.getStatus(new GetQuoteException(NOT_FOUND)), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(errorHandler.getStatus(new RuntimeException(NOT_FOUND)), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
