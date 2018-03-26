package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GeoLocationNotFoundException;
import org.learning.by.example.reactive.microservices.exceptions.GetGeoLocationException;
import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.learning.by.example.reactive.microservices.model.GeoLocationResponse;
import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.test.tags.UnitTest;
import org.springframework.beans.factory.annotation.Value;
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
class GeoLocationServiceImplTest {

    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final String GOOGLE_ADDRESS_IN_PARAMS = "?address=" + GOOGLE_ADDRESS;
    private static final Mono<String> GOOGLE_ADDRESS_MONO = Mono.just(GOOGLE_ADDRESS);
    private static final String BAD_EXCEPTION = "bad exception";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String OK_STATUS = "OK";

    @SpyBean(GeoLocationService.class)
    private GeoLocationServiceImpl locationService;

    private static final String JSON_OK = "/json/GeoLocationResponse_OK.json";
    private static final String JSON_NOT_FOUND = "/json/GeoLocationResponse_NOT_FOUND.json";
    private static final String JSON_EMPTY = "/json/GeoLocationResponse_EMPTY.json";
    private static final String JSON_WRONG_STATUS = "/json/GeoLocationResponse_WRONG_STATUS.json";

    private static final Mono<GeoLocationResponse> LOCATION_OK = getMonoFromJsonPath(JSON_OK, GeoLocationResponse.class);
    private static final Mono<GeoLocationResponse> LOCATION_NOT_FOUND = getMonoFromJsonPath(JSON_NOT_FOUND, GeoLocationResponse.class);
    private static final Mono<GeoLocationResponse> LOCATION_EMPTY = getMonoFromJsonPath(JSON_EMPTY, GeoLocationResponse.class);
    private static final Mono<GeoLocationResponse> LOCATION_WRONG_STATUS = getMonoFromJsonPath(JSON_WRONG_STATUS, GeoLocationResponse.class);
    private static final Mono<GeoLocationResponse> LOCATION_EXCEPTION = Mono.error(new GetGeoLocationException(BAD_EXCEPTION));
    private static final Mono<GeoLocationResponse> BIG_EXCEPTION = Mono.error(new RuntimeException(BAD_EXCEPTION));

    @Value("${GeoLocationServiceImpl.endPoint}")
    private String endPoint;

    @Test
    void getBeamTest() {
        assertThat(locationService, is(notNullValue()));
    }

    @Test
    void getMockingWebClientTest() {
        locationService.webClient = mockWebClient(locationService.webClient, LOCATION_OK);

        final GeoLocationResponse location = GOOGLE_ADDRESS_MONO.transform(locationService::get).block();
        assertThat(location.getStatus(), is(OK_STATUS));

        reset(locationService.webClient);
    }

    @Test
    void fromAddressTest() {
        doReturn(LOCATION_OK).when(locationService).get(any());

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress).block();

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

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GeoLocationNotFoundException.class));
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

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
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

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
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

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
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

        final GeographicCoordinates geographicCoordinates = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
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
    void buildUrlTest() {
        final String url = GOOGLE_ADDRESS_MONO.transform(locationService::buildUrl).block();

        assertThat(url, is(notNullValue()));
        assertThat(url, is(endPoint.concat(GOOGLE_ADDRESS_IN_PARAMS)));
    }

    @Test
    void buildUrlEmptyAddressTest() {
        final String url = Mono.just("").transform(locationService::buildUrl)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(InvalidParametersException.class));
                    return Mono.empty();
                }).block();

        assertThat(url, is(nullValue()));
    }
}
