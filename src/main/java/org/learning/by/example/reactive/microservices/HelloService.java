package org.learning.by.example.reactive.microservices;

import reactor.core.publisher.Mono;

import java.util.function.Function;


public class HelloService {
    static Function<Mono<String>, Mono<String>> getHello() {
        return value -> value.flatMap(name ->{
            if (name.equals("")) {
                return Mono.error(new InvalidParametersException("bad parameters"));
            }
            return Mono.just(name);
        });
    }
}
