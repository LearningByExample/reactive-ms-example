package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetLocationException;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.LocationResult;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class LocationServiceImpl implements LocationService {

    private static final String OK_STATUS = "OK";
    private static final String ZERO_RESULTS = "ZERO_RESULTS";
    private static final String ERROR_GETTING_LOCATION = "error getting location";
    private static final String LOCATION_NOT_FOUND = "location not found";
    private static final String ADDRESS_PARAMETER = "?address=";
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
                .uri(endPoint.concat(ADDRESS_PARAMETER).concat(address))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(LocationResult.class))
        );
    }

    @Override
    public Mono<Location> fromAddress(Mono<String> address) {
        return address.transform(this::request)
            .onErrorResume(throwable -> Mono.error(new GetLocationException(ERROR_GETTING_LOCATION, throwable)) )
            .transform(this::geometryLocation);
    }

    private Mono<Location> geometryLocation(Mono<LocationResult> location){
        return location.flatMap(locationResult -> {
            switch (locationResult.getStatus()) {
                case OK_STATUS:
                    return Mono.just(
                            new Location(locationResult.getResults()[0].getGeometry().getLocation().getLat(),
                                    locationResult.getResults()[0].getGeometry().getLocation().getLng()));
                case ZERO_RESULTS:
                    return Mono.error(new LocationNotFoundException(LOCATION_NOT_FOUND));
                default:
                    return Mono.error(new GetLocationException(ERROR_GETTING_LOCATION));
            }}
        );
    }
}
