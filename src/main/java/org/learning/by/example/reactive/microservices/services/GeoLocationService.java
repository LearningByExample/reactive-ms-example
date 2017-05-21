package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import reactor.core.publisher.Mono;

public interface GeoLocationService {

    Mono<GeographicCoordinates> fromAddress(Mono<String> addressMono);
}
