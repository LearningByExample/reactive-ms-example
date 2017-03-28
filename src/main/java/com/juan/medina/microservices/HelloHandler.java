package com.juan.medina.microservices;

import com.juan.medina.microservices.model.HelloRequest;
import com.juan.medina.microservices.model.HelloResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Created by mail on 27/03/2017.
 */


class HelloHandler {

    private Mono<ServerResponse> getResponse(String value){
        return ServerResponse.ok().body(Mono.just(new HelloResponse(value)), HelloResponse.class);
    }


    Mono<ServerResponse> getHello() {
        return getResponse("world");
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).then(
                it -> getResponse(it.getName())
        );
    }
}