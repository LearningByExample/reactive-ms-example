package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetSunriseSunsetException;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.model.SunriseSunsetResult;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SunriseSunsetServiceImpl implements SunriseSunsetService {

    private static final String BEGIN_PARAMETERS = "?";
    private static final String NEXT_PARAMETER = "&";
    private static final String EQUALS = "=";
    private static final String LATITUDE_PARAMETER = "lat" + EQUALS;
    private static final String LONGITUDE_PARAMETER = "lng" + EQUALS;
    private static final String ERROR_GETTING_DATA = "error getting sunrise and sunset";
    private static final String SUNRISE_RESULT_NOT_OK = "sunrise result was not OK";
    private static final String STATUS_OK = "OK";

    WebClient webClient;
    private final String endPoint;

    public SunriseSunsetServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    @Override
    public Mono<SunriseSunset> fromLocation(Mono<Location> location) {
        return location
                .transform(this::buildUrl)
                .transform(this::get)
                .onErrorResume(throwable -> Mono.error(new GetSunriseSunsetException(ERROR_GETTING_DATA, throwable)))
                .transform(this::createResult);
    }

    Mono<String> buildUrl(final Mono<Location> locationMono) {
        return locationMono.flatMap(location -> Mono.just(endPoint.concat(BEGIN_PARAMETERS)
                .concat(LATITUDE_PARAMETER).concat(Double.toString(location.getLatitude()))
                .concat(NEXT_PARAMETER)
                .concat(LONGITUDE_PARAMETER).concat(Double.toString(location.getLongitude()))
        ));
    }

    Mono<SunriseSunsetResult> get(final Mono<String> monoUrl) {
        return monoUrl.flatMap(url -> webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(SunriseSunsetResult.class)));
    }

    Mono<SunriseSunset> createResult(final Mono<SunriseSunsetResult> sunriseSunsetResultMono) {
        return sunriseSunsetResultMono.flatMap(sunriseSunsetResult -> {
            if ((sunriseSunsetResult.getStatus() != null) && (sunriseSunsetResult.getStatus().equals(STATUS_OK))) {
                return Mono.just(new SunriseSunset(sunriseSunsetResult.getResults().getSunrise(),
                        sunriseSunsetResult.getResults().getSunset()));
            } else {
                return Mono.error(new GetSunriseSunsetException(SUNRISE_RESULT_NOT_OK));
            }
        });
    }
}
