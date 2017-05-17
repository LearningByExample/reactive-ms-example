package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.model.Location;
import reactor.core.publisher.Mono;

public interface LocationService {
    Mono<Location> fromAddress(Mono<String> address);
}
