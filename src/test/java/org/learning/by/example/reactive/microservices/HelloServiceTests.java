package org.learning.by.example.reactive.microservices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloServiceTests {

    @Autowired
    private HelloService helloService;

    private static final Mono<String> VALID_VALUE = Mono.just("VALID");
    private static final Mono<String> INVALID_VALUE = Mono.just("");

    @Test
    public void validValue() {
        VALID_VALUE.publish(helloService.getGreetings()).subscribe(value ->
            assertThat(value, is(VALID_VALUE.block()))
        );
    }

    @Test
    public void invalidValueTest(){
        INVALID_VALUE.publish(helloService.getGreetings()).subscribe(value ->{
            throw new NotImplementedException();
        }, exception -> {
            assertThat(exception, instanceOf(InvalidParametersException.class));
        });
    }
}
