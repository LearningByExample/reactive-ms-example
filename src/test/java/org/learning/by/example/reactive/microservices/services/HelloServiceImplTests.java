package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@UnitTest
@DisplayName("HelloServiceImpl Unit Tests")
@SpringBootTest(classes = ReactiveMsApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class HelloServiceImplTests {
    @Autowired
    private HelloServiceImpl helloService;

    private static final String VALID = "VALID";
    private static final String EMPTY = "";
    private static final Mono<String> VALID_VALUE = Mono.just(VALID);
    private static final Mono<String> INVALID_VALUE = Mono.just(EMPTY);
    private static final String SHOULD_NOT_RETURN_OBJECT = "Shouldn't get a object";

    @Test
    void validValue() {
        VALID_VALUE.publish(helloService::greetings).subscribe(value ->
            assertThat(value, is(VALID))
        );
    }

    @Test
    void invalidValueTest() {
        INVALID_VALUE.publish(helloService::greetings).subscribe(value -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, exception -> {
            assertThat(exception, instanceOf(InvalidParametersException.class));
        });
    }
}
