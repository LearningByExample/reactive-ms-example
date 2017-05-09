package org.learning.by.example.reactive.microservices.handlers;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactiveMsApplication.class)
@ActiveProfiles("test")
@Category(UnitTest.class)
public class ThrowableTranslatorTest {

    @Factory
    public static DiagnosingMatcher<Object> translateTo(HttpStatus status) {
        return new DiagnosingMatcher<Object>() {
            private static final String EXCEPTION = "EXCEPTION";

            @Override
            public void describeTo(final Description description) {
                description.appendText("does not translate to ").appendText(status.toString());
            }

            @SuppressWarnings("unchecked")
            protected boolean matches(final Object item, final Description mismatch) {

                if (item instanceof Class) {
                    if(((Class)item).getClass().isInstance(Throwable.class )){
                        Class<? extends Throwable> type = (Class<? extends Throwable>) item;
                        try {
                            Throwable exception = type.getConstructor(String.class).newInstance(EXCEPTION);
                            Mono.just(exception).publish(ThrowableTranslator::translate).subscribe(translator -> {
                                assertThat(translator.getMessage(), is(EXCEPTION));
                                assertThat(translator.getHttpStatus(), is(status));
                            });
                        } catch (InstantiationException | IllegalAccessException |
                                InvocationTargetException | NoSuchMethodException cause) {
                            throw new AssertionError("This exception class has not constructor with a String", cause);
                        }
                        return true;
                    }
                }
                mismatch.appendText(item.toString());
                return false;
            }
        };
    }

    @Test
    public void translateGetQuoteExceptionTest() throws Exception {
        assertThat(GetQuoteException.class, translateTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void translateInvalidParametersExceptionTest() throws Exception {
        assertThat(InvalidParametersException.class, translateTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void translatePathNotFoundExceptionTest() throws Exception {
        assertThat(PathNotFoundException.class, translateTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void translateGenericExceptionTest() throws Exception {
        assertThat(Exception.class, translateTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
