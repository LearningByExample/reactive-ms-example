package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.LocationResult;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class LocationServiceImpl implements LocationService {

    private static final String OK_STATUS = "OK";
    private static final String ERROR_GETTING_LOCATION = "error getting location";
    private static final String ADDRESS = "?address=";
    private final WebClient webClient;
    private final String endPoint;

    public LocationServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    Mono<LocationResult> request(final Mono<String> addressMono) {
        return addressMono.flatMap(address ->
                webClient
                        .get()
                        .uri(endPoint.concat(ADDRESS).concat(address))
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .flatMap(clientResponse -> clientResponse.bodyToMono(LocationResult.class))
        );
    }

    @Override
    public Mono<Location> fromAddress(Mono<String> address) {

        return address.publish(this::request).flatMap(locationResult -> {
            if (!locationResult.getStatus().equals(OK_STATUS)) {
                return Mono.error(new RuntimeException(ERROR_GETTING_LOCATION));
            } else {
                if (locationResult.getResults().length == 0) {
                    return Mono.error(new RuntimeException(ERROR_GETTING_LOCATION));
                } else
                    return Mono.just(new Location(locationResult.getResults()[0].getGeometry().getLocation().getLat(),
                            locationResult.getResults()[0].getGeometry().getLocation().getLng()));
            }
        });
    }
}
