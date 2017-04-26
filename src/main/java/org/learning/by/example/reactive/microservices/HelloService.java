package org.learning.by.example.reactive.microservices;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
class HelloService {
    Function<Mono<String>, Mono<String>> getGreetings() {
        return value -> value.flatMap(name ->{
            if (name.equals("")) {
                return Mono.error(new InvalidParametersException("bad parameters"));
            }
            return Mono.just(name);
        });
    }
}
