package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ApiHandler {

    private static final String NAME = "name";

    private final ErrorHandler errorHandler;
    private final HelloService helloService;
    private final QuoteService quoteService;

    private static final Mono<String> DEFAULT_NAME = Mono.just("world");

    public ApiHandler(final HelloService helloService, final QuoteService quoteService, final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.helloService = helloService;
        this.quoteService = quoteService;
    }

    public Mono<ServerResponse> defaultHello(final ServerRequest request) {
        return DEFAULT_NAME
                .transform(this::getServerResponse)
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> getHello(final ServerRequest request) {
        return Mono.just(request.pathVariable(NAME))
                .transform(this::getServerResponse)
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> postHello(final ServerRequest request) {
        return request.bodyToMono(HelloRequest.class)
                .flatMap(helloRequest -> Mono.just(helloRequest.getName()))
                .transform(this::getServerResponse)
                .onErrorResume(errorHandler::throwableError);
    }

    Mono<ServerResponse> getServerResponse(final Mono<String> monoName) {
        return monoName.transform(this::createHelloResponse)
                .transform(this::convertToServerResponse);
    }

    Mono<HelloResponse> createHelloResponse(final Mono<String> monoName) {
        return monoName.transform(helloService::greetings)
                .and(Mono.defer(quoteService::get), this::combineGreetingAndQuote);
    }

    HelloResponse combineGreetingAndQuote(final String greeting, final Quote quote) {
        return new HelloResponse(greeting, quote.getContent());
    }

    Mono<ServerResponse> convertToServerResponse(final Mono<HelloResponse> helloResponse) {
        return ServerResponse.ok().body(helloResponse, HelloResponse.class);
    }
}