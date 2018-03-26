package org.learning.by.example.reactive.microservices.handlers;

import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.LocationRequest;
import org.learning.by.example.reactive.microservices.model.LocationResponse;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.services.GeoLocationService;
import org.learning.by.example.reactive.microservices.services.SunriseSunsetService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ApiHandler {

  private static final String ADDRESS = "address";
  private static final String EMPTY_STRING = "";

  private final ErrorHandler errorHandler;

  private final GeoLocationService geoLocationService;
  private final SunriseSunsetService sunriseSunsetService;

  public ApiHandler(final GeoLocationService geoLocationService, final SunriseSunsetService sunriseSunsetService,
                    final ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    this.geoLocationService = geoLocationService;
    this.sunriseSunsetService = sunriseSunsetService;
  }

  public Mono<ServerResponse> postLocation(final ServerRequest request) {
    return request.bodyToMono(LocationRequest.class)
        .map(LocationRequest::getAddress)
        .onErrorResume(throwable -> Mono.just(EMPTY_STRING))
        .transform(this::buildResponse)
        .onErrorResume(errorHandler::throwableError);
  }

  public Mono<ServerResponse> getLocation(final ServerRequest request) {
    return Mono.just(request.pathVariable(ADDRESS))
        .transform(this::buildResponse)
        .onErrorResume(errorHandler::throwableError);
  }

  Mono<ServerResponse> buildResponse(final Mono<String> address) {
    return address
        .transform(geoLocationService::fromAddress)
        .zipWhen(this::sunriseSunset, LocationResponse::new)
        .transform(this::serverResponse);
  }

  private Mono<SunriseSunset> sunriseSunset(GeographicCoordinates geographicCoordinates) {
    return Mono.just(geographicCoordinates).transform(sunriseSunsetService::fromGeographicCoordinates);
  }

  Mono<ServerResponse> serverResponse(Mono<LocationResponse> locationResponseMono) {
    return locationResponseMono.flatMap(locationResponse ->
        ServerResponse.ok().body(Mono.just(locationResponse), LocationResponse.class));
  }
}