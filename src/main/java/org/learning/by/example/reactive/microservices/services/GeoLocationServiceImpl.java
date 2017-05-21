package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetGeoLocationException;
import org.learning.by.example.reactive.microservices.exceptions.GeoLocationNotFoundException;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.GeoLocationResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GeoLocationServiceImpl implements GeoLocationService {

    private static final String OK_STATUS = "OK";
    private static final String ZERO_RESULTS = "ZERO_RESULTS";
    private static final String ERROR_GETTING_LOCATION = "error getting location";
    private static final String ERROR_LOCATION_WAS_NULL = "error location was null";
    private static final String ADDRESS_NOT_FOUND = "address not found";
    private static final String ADDRESS_PARAMETER = "?address=";
    private static final String MISSING_ADDRESS = "missing address";
    WebClient webClient;
    private final String endPoint;

    public GeoLocationServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    @Override
    public Mono<GeographicCoordinates> fromAddress(final Mono<String> addressMono) {
        return addressMono
                .transform(this::buildUrl)
                .transform(this::get)
                .onErrorResume(throwable -> Mono.error(new GetGeoLocationException(ERROR_GETTING_LOCATION, throwable)))
                .transform(this::geometryLocation);
    }

    Mono<String> buildUrl(final Mono<String> addressMono) {
        return addressMono.flatMap(address -> {
            if (address.equals("")) {
                return Mono.error(new InvalidParametersException(MISSING_ADDRESS));
            }
            return Mono.just(endPoint.concat(ADDRESS_PARAMETER).concat(address));
        });
    }

    Mono<GeoLocationResponse> get(final Mono<String> urlMono) {
        return urlMono.flatMap(url -> webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(GeoLocationResponse.class)));
    }

    Mono<GeographicCoordinates> geometryLocation(final Mono<GeoLocationResponse> geoLocationResponseMono) {
        return geoLocationResponseMono.flatMap(geoLocationResponse -> {
                    if (geoLocationResponse.getStatus() != null) {
                        switch (geoLocationResponse.getStatus()) {
                            case OK_STATUS:
                                return Mono.just(
                                        new GeographicCoordinates(geoLocationResponse.getResults()[0].getGeometry().getLocation().getLat(),
                                                geoLocationResponse.getResults()[0].getGeometry().getLocation().getLng()));
                            case ZERO_RESULTS:
                                return Mono.error(new GeoLocationNotFoundException(ADDRESS_NOT_FOUND));
                            default:
                                return Mono.error(new GetGeoLocationException(ERROR_GETTING_LOCATION));
                        }
                    } else {
                        return Mono.error(new GetGeoLocationException(ERROR_LOCATION_WAS_NULL));
                    }
                }
        );
    }
}
