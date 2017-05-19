package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.model.SunriseSunsetResult;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.learning.by.example.reactive.microservices.test.RestServiceHelper.getMonoFromJsonPath;
import static org.learning.by.example.reactive.microservices.test.RestServiceHelper.mockWebClient;
import static org.mockito.Mockito.reset;

@UnitTest
@DisplayName("SunriseSunsetServiceImplTests Unit Tests")
class SunriseSunsetServiceImplTests {

    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final Location GOOGLE_LOCATION = new Location(GOOGLE_LAT, GOOGLE_LNG);
    private static final Mono<Location> GOOGLE_LOCATION_MONO = Mono.just(GOOGLE_LOCATION);
    private static final String GOOGLE_LOCATION_IN_PARAMS = "?lat="+Double.toString(GOOGLE_LAT)+"&lng="+Double.toString(GOOGLE_LNG);

    private static final String JSON_OK = "/json/SunriseSunsetResult_OK.json";
    private static final Mono<SunriseSunsetResult> SUNRISE_SUNSET_OK = getMonoFromJsonPath(JSON_OK, SunriseSunsetResult.class);
    private static final String OK = "OK";

    @Value("${SunriseSunsetServiceImpl.endPoint}")
    private
    String endPoint;

    @SpyBean(SunriseSunsetService.class)
    private SunriseSunsetServiceImpl sunriseSunsetService;

    @Test
    void getBeamTest() {
        assertThat(sunriseSunsetService, is(notNullValue()));
    }

    @Test
    void getMockingWebClientTest() {
        sunriseSunsetService.webClient = mockWebClient(sunriseSunsetService.webClient, SUNRISE_SUNSET_OK);

        SunriseSunsetResult result = Mono.just(endPoint.concat(GOOGLE_LOCATION_IN_PARAMS)).transform(sunriseSunsetService::get).block();

        assertThat(result, is(notNullValue()));
        assertThat(result.getResults(), is(notNullValue()));
        assertThat(result.getStatus(), is(OK));

        reset(sunriseSunsetService.webClient);
    }

    @Test
    void fromLocationTest() {
        SunriseSunset result = GOOGLE_LOCATION_MONO.transform(sunriseSunsetService::fromLocation).block();

        assertThat(result, is(notNullValue()));
        assertThat(result.getSunrise(), not(isEmptyOrNullString()));
        assertThat(result.getSunset(), not(isEmptyOrNullString()));
    }

    @Test
    void buildUrlTest() {
        String url = GOOGLE_LOCATION_MONO.transform(sunriseSunsetService::buildUrl).block();

        assertThat(url, is(notNullValue()));
        assertThat(url, is(endPoint.concat(GOOGLE_LOCATION_IN_PARAMS)));
    }
}
