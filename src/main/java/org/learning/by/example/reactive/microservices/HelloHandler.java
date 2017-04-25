package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class HelloHandler {

    private static Logger logger = LoggerFactory.getLogger(HelloHandler.class);

    private ErrorHandler error;

    private static final String DEFAULT_VALUE = "world";

    HelloHandler() {
        error = new ErrorHandler();
    }

    private Mono<ServerResponse> getResponse(String value) {
        if (value.equals("")) {
            return Mono.error(new InvalidParametersException("bad parameters"));
        }
        return ServerResponse.ok().body(Mono.just(new HelloResponse(value)), HelloResponse.class);
    }

    private Mono<ServerResponse> getResponseWeb(String value) {
        if (value.equals("")) {
            return Mono.error(new InvalidParametersException("bad parameters"));
        }
        String content = "demo";
        try {
            final StringBuilder path = new StringBuilder()
                .append(new java.io.File( "." ).getCanonicalPath())
                .append("/src/main/resources/static/index.html");
            content = new String(Files.readAllBytes(Paths.get(path.toString())));
        }catch(IOException e){
            logger.error("Content not loaded");
        }
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(Mono.just(content), String.class);
    }

    Mono<ServerResponse> defaultHello(ServerRequest request) {
        return getResponse(DEFAULT_VALUE);
    }

    Mono<ServerResponse> getWebsite(ServerRequest request) {
        return getResponseWeb(DEFAULT_VALUE);
    }

    Mono<ServerResponse> getHello(ServerRequest request) {
        return getResponse(request.pathVariable("name"));
    }

    Mono<ServerResponse> postHello(ServerRequest request) {
        return request.bodyToMono(HelloRequest.class).flatMap(helloRequest -> getResponse(helloRequest.getName()))
                .onErrorResume(error::badRequest);
    }
}