package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetSunriseSunsetException;
import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.model.GeoTimesResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SunriseSunsetServiceImpl implements SunriseSunsetService {

    private static final String BEGIN_PARAMETERS = "?";
    private static final String NEXT_PARAMETER = "&";
    private static final String EQUALS = "=";
    private static final String LATITUDE_PARAMETER = "lat" + EQUALS;
    private static final String LONGITUDE_PARAMETER = "lng" + EQUALS;
    private static final String DATE_PARAMETER = "date" + EQUALS;
    private static final String TODAY_DATE = "today";
    private static final String FORMATTED_PARAMETER = "formatted" + EQUALS;
    private static final String NOT_FORMATTED = "0";
    private static final String ERROR_GETTING_DATA = "error getting sunrise and sunset";
    private static final String SUNRISE_RESULT_NOT_OK = "sunrise and sunrise result was not OK";
    private static final String STATUS_OK = "OK";

    WebClient webClient;
    private final String endPoint;

    public SunriseSunsetServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    @Override
    public Mono<SunriseSunset> fromGeographicCoordinates(Mono<GeographicCoordinates> location) {
        return location
                .transform(this::buildUrl)
                .transform(this::get)
                .onErrorResume(throwable -> Mono.error(new GetSunriseSunsetException(ERROR_GETTING_DATA, throwable)))
                .transform(this::createResult);
    }

    Mono<String> buildUrl(final Mono<GeographicCoordinates> geographicCoordinatesMono) {
        return geographicCoordinatesMono.flatMap(geographicCoordinates -> Mono.just(endPoint
                .concat(BEGIN_PARAMETERS)
                .concat(LATITUDE_PARAMETER).concat(Double.toString(geographicCoordinates.getLatitude()))
                .concat(NEXT_PARAMETER)
                .concat(LONGITUDE_PARAMETER).concat(Double.toString(geographicCoordinates.getLongitude()))
                .concat(NEXT_PARAMETER)
                .concat(DATE_PARAMETER).concat(TODAY_DATE)
                .concat(NEXT_PARAMETER)
                .concat(FORMATTED_PARAMETER).concat(NOT_FORMATTED)
        ));
    }

    Mono<GeoTimesResponse> get(final Mono<String> monoUrl) {
        return monoUrl.flatMap(url -> webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(GeoTimesResponse.class)));
    }

    Mono<SunriseSunset> createResult(final Mono<GeoTimesResponse> geoTimesResponseMono) {
        return geoTimesResponseMono.flatMap(geoTimesResponse -> {
            if ((geoTimesResponse.getStatus() != null) && (geoTimesResponse.getStatus().equals(STATUS_OK))) {
                return Mono.just(new SunriseSunset(geoTimesResponse.getResults().getSunrise(),
                        geoTimesResponse.getResults().getSunset()));
            } else {
                return Mono.error(new GetSunriseSunsetException(SUNRISE_RESULT_NOT_OK));
            }
        });
    }
}
