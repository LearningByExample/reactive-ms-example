package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GetGeoLocationException;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.GeocodeResult;
import org.learning.by.example.reactive.microservices.test.tags.UnitTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.learning.by.example.reactive.microservices.test.RestServiceHelper.getMonoFromJsonPath;
import static org.learning.by.example.reactive.microservices.test.RestServiceHelper.mockWebClient;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@DisplayName("GeoLocationServiceImpl Unit Tests")
class GeoLocationServiceImplTests {

    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final Mono<String> GOOGLE_ADDRESS_MONO = Mono.just(GOOGLE_ADDRESS);
    private static final String BAD_EXCEPTION = "bad exception";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String OK_STATUS = "OK";

    @SpyBean(GeoLocationService.class)
    private GeoLocationServiceImpl locationService;

    private static final String JSON_OK = "/json/GeocodeResult_OK.json";
    private static final String JSON_NOT_FOUND = "/json/GeocodeResult_NOT_FOUND.json";
    private static final String JSON_EMPTY = "/json/GeocodeResult_EMPTY.json";
    private static final String JSON_WRONG_STATUS = "/json/GeocodeResult_WRONG_STATUS.json";

    private static final Mono<GeocodeResult> LOCATION_OK = getMonoFromJsonPath(JSON_OK, GeocodeResult.class);
    private static final Mono<GeocodeResult> LOCATION_NOT_FOUND = getMonoFromJsonPath(JSON_NOT_FOUND, GeocodeResult.class);
    private static final Mono<GeocodeResult> LOCATION_EMPTY = getMonoFromJsonPath(JSON_EMPTY, GeocodeResult.class);
    private static final Mono<GeocodeResult> LOCATION_WRONG_STATUS = getMonoFromJsonPath(JSON_WRONG_STATUS, GeocodeResult.class);
    private static final Mono<GeocodeResult> LOCATION_EXCEPTION = Mono.error(new GetGeoLocationException(BAD_EXCEPTION));
    private static final Mono<GeocodeResult> BIG_EXCEPTION = Mono.error(new RuntimeException(BAD_EXCEPTION));

    @Test
    void getBeamTest() {
        assertThat(locationService, is(notNullValue()));
    }

    @Test
    void getMockingWebClientTest() {
        locationService.webClient = mockWebClient(locationService.webClient, LOCATION_OK);

        GeocodeResult location = GOOGLE_ADDRESS_MONO.transform(locationService::get).block();
        assertThat(location.getStatus(), is(OK_STATUS));

        reset(locationService.webClient);
    }

    @Test
    void fromAddressTest() {
        doReturn(LOCATION_OK).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress).block();

        assertThat(geographicCoordinates, is(notNullValue()));
        assertThat(geographicCoordinates.getLatitude(), is(GOOGLE_LAT));
        assertThat(geographicCoordinates.getLongitude(), is(GOOGLE_LNG));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressNotFoundTest() {
        doReturn(LOCATION_NOT_FOUND).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(LocationNotFoundException.class));
                    return Mono.empty();
                }).block();

        assertThat(geographicCoordinates, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressExceptionTest() {
        doReturn(LOCATION_EXCEPTION).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetGeoLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(geographicCoordinates, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressBigExceptionTest() {
        doReturn(BIG_EXCEPTION).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetGeoLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(geographicCoordinates, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressEmptyTest() {
        doReturn(LOCATION_EMPTY).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetGeoLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(geographicCoordinates, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressWrongStatusTest() {
        doReturn(LOCATION_WRONG_STATUS).when(locationService).get(any());

        GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetGeoLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(geographicCoordinates, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }
}
