package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetLocationException;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.model.SunriseSunsetResult;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SunriseSunsetServiceImpl implements SunriseSunsetService{

    private static final String BEGIN_PARAMETERS="?";
    private static final String NEXT_PARAMETER="&";
    private static final String EQUALS="=";
    private static final String LATITUDE_PARAMETER="lat";
    private static final String LONGITUDE_PARAMETER="lng";

    WebClient webClient;
    private final String endPoint;

    public SunriseSunsetServiceImpl(final String endPoint){
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    @Override
    public Mono<SunriseSunset> fromLocation(Mono<Location> location) {
        return location
                .transform(this::buildUrl)
                .transform(this::get)
                .onErrorResume(throwable -> Mono.error(new GetLocationException("ERROR", throwable)))
                .transform(this::createResult);
    }

    Mono<String> buildUrl(final Mono<Location> locationMono) {
        return locationMono.flatMap(location -> Mono.just(endPoint
                .concat(BEGIN_PARAMETERS)
                .concat(LATITUDE_PARAMETER).concat(EQUALS).concat(Double.toString(location.getLatitude()))
                .concat(NEXT_PARAMETER)
                .concat(LONGITUDE_PARAMETER).concat(EQUALS).concat(Double.toString(location.getLongitude()))
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

    Mono<SunriseSunset> createResult(final Mono<SunriseSunsetResult> sunriseSunsetResultMono){
        return sunriseSunsetResultMono.flatMap(sunriseSunsetResult ->
                Mono.just(new SunriseSunset(sunriseSunsetResult.results.sunrise, sunriseSunsetResult.results.sunset)));
    }
}
