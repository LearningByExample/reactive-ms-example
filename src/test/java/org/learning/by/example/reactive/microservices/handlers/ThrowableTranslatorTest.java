package org.learning.by.example.reactive.microservices.handlers;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.exceptions.GetQuoteException;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.exceptions.PathNotFoundException;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.learning.by.example.reactive.microservices.handlers.ThrowableTranslator.translate;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Category(UnitTest.class)
public class ThrowableTranslatorTest {

    private static final String EXCEPTION = "EXCEPTION";

    @Test
    public void translateGetQuoteExceptionTest() throws Exception {
        check(GetQuoteException.class, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void translateInvalidParametersExceptionTest() throws Exception {
        check(InvalidParametersException.class, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void translatePathNotFoundExceptionTest() throws Exception {
        check(PathNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @Test
    public void translateGenericExceptionTest() throws Exception {
        check(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static <T extends Throwable> void check(Class<T> type, HttpStatus status) {
        try {
            T exception = type.getConstructor(String.class).newInstance(EXCEPTION);
            Mono.just(exception).publish(translate()).subscribe(translator -> {
                assertThat(translator.getMessage(), is(EXCEPTION));
                assertThat(translator.getHttpStatus(), is(status));
            });
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException cause) {
            throw new AssertionError("This exception class has not constructor with a String", cause);
        }
    }
}


