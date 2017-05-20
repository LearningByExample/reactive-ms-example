package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import reactor.core.publisher.Mono;

public interface SunriseSunsetService {
    Mono<SunriseSunset> fromGeographicCoordinates(Mono<GeographicCoordinates> location);
}
