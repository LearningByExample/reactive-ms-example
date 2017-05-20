package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.LocationService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.services.SunriseSunsetService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ApiHandler {

    private static final String NAME = "name";
    private static final String ADDRESS = "address";

    private final ErrorHandler errorHandler;
    private final HelloService helloService;
    private final QuoteService quoteService;
    private final LocationService locationService;
    private final SunriseSunsetService sunriseSunsetService;

    private static final Mono<String> DEFAULT_NAME = Mono.just("world");

    public ApiHandler(final HelloService helloService, final QuoteService quoteService,
                      final LocationService locationService, final SunriseSunsetService sunriseSunsetService,
                      final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.helloService = helloService;
        this.quoteService = quoteService;
        this.locationService = locationService;
        this.sunriseSunsetService = sunriseSunsetService;
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

    Mono<ServerResponse> convertToServerResponse(final Mono<HelloResponse> helloResponseMono) {
        return helloResponseMono.flatMap(helloResponse ->
                ServerResponse.ok().body(Mono.just(helloResponse), HelloResponse.class));
    }

    public Mono<ServerResponse> getLocation(final ServerRequest request){
        return Mono.just(request.pathVariable(ADDRESS))
                .transform(locationService::fromAddress)
                .and(this::sunriseSunset, LocationResponse::new)
                .transform(this::response)
                .onErrorResume(errorHandler::throwableError);
    }

    private Mono<SunriseSunset> sunriseSunset(Location location){
        return Mono.just(location).transform(sunriseSunsetService::fromLocation);
    }
    Mono<ServerResponse> response(Mono<LocationResponse> locationResponseMono) {
        return locationResponseMono.flatMap(locationResponse ->
                ServerResponse.ok().body(Mono.just(locationResponse), LocationResponse.class));
    }
}