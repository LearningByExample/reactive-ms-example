package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

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
                .publish(getServerResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> getHello(final ServerRequest request) {
        return Mono.just(request.pathVariable(NAME))
                .publish(getServerResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<ServerResponse> postHello(final ServerRequest request) {
        return request.bodyToMono(HelloRequest.class)
                .flatMap(helloRequest -> Mono.just(helloRequest.getName()))
                .publish(getServerResponse())
                .onErrorResume(errorHandler::throwableError);
    }

    Function<Mono<String>, Mono<ServerResponse>> getServerResponse() {
        return (name) -> name
                .publish(createHelloResponse())
                .publish(convertToServerResponse());
    }

    Function<Mono<String>, Mono<HelloResponse>> createHelloResponse() {
        return name ->
                name.publish(helloService.getGreetings()).flatMap(
                        greetings -> getQuote().flatMap(
                                title -> Mono.just(new HelloResponse(greetings, title))));
    }

    Mono<String> getQuote() {
        return Mono.fromSupplier(quoteService.getQuote())
                .flatMap(quoteMono -> quoteMono.flatMap(quote -> Mono.just(quote.getContent())));
    }

    Function<Mono<HelloResponse>, Mono<ServerResponse>> convertToServerResponse() {
        return value -> value.flatMap(helloResponse -> {
            return ServerResponse.ok().body(Mono.just(helloResponse), HelloResponse.class);
        });
    }
}