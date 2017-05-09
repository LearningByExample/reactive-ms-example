package org.learning.by.example.reactive.microservices.services;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactiveMsApplication.class)
@ActiveProfiles("test")
@Category(UnitTest.class)
public class HelloServiceImplTests {
    @Autowired
    private HelloServiceImpl helloService;

    private static final String VALID = "VALID";
    private static final String EMPTY = "";
    private static final Mono<String> VALID_VALUE = Mono.just(VALID);
    private static final Mono<String> INVALID_VALUE = Mono.just(EMPTY);
    private static final String SHOULD_NOT_RETURN_OBJECT = "Shouldn't get a object";

    @Test
    public void validValue() {
        VALID_VALUE.publish(helloService::greetings).subscribe(value ->
            assertThat(value, is(VALID))
        );
    }

    @Test
    public void invalidValueTest() {
        INVALID_VALUE.publish(helloService::greetings).subscribe(value -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, exception -> {
            assertThat(exception, instanceOf(InvalidParametersException.class));
        });
    }
}
