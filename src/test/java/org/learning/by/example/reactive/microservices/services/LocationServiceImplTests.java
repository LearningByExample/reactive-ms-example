package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GetLocationException;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.LocationResult;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
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
@DisplayName("LocationServiceImpl Unit Tests")
class LocationServiceImplTests {

    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final Mono<String> GOOGLE_ADDRESS_MONO = Mono.just(GOOGLE_ADDRESS);
    private static final String BAD_EXCEPTION = "bad exception";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String OK_STATUS = "OK";

    @SpyBean(LocationService.class)
    private LocationServiceImpl locationService;

    static private Mono<LocationResult> getLocationResultFromJsonPath(final String jsonPath) {
        return getMonoFromJsonPath(jsonPath, LocationResult.class);
    }

    private static final String JSON_OK = "/json/LocationResult_OK.json";
    private static final String JSON_NOT_FOUND = "/json/LocationResult_NOT_FOUND.json";
    private static final String JSON_EMPTY = "/json/LocationResult_EMPTY.json";
    private static final String JSON_WRONG_STATUS = "/json/LocationResult_WRONG_STATUS.json";

    private static final Mono<LocationResult> LOCATION_OK = getLocationResultFromJsonPath(JSON_OK);
    private static final Mono<LocationResult> LOCATION_NOT_FOUND = getLocationResultFromJsonPath(JSON_NOT_FOUND);
    private static final Mono<LocationResult> LOCATION_EMPTY = getLocationResultFromJsonPath(JSON_EMPTY);
    private static final Mono<LocationResult> LOCATION_WRONG_STATUS = getLocationResultFromJsonPath(JSON_WRONG_STATUS);
    private static final Mono<LocationResult> LOCATION_EXCEPTION = Mono.error(new GetLocationException(BAD_EXCEPTION));

    @Test
    void getBeamTest() {
        assertThat(locationService, is(notNullValue()));
    }

    @Test
    void getMockingWebClientTest() {
        locationService.webClient = mockWebClient(locationService.webClient, LOCATION_OK);

        LocationResult location = GOOGLE_ADDRESS_MONO.transform(locationService::get).block();
        assertThat(location.getStatus(), is(OK_STATUS));

        reset(locationService.webClient);
    }

    @Test
    void fromAddressTest() {
        doReturn(LOCATION_OK).when(locationService).get(any());

        Location location = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress).block();

        assertThat(location, is(notNullValue()));
        assertThat(location.getLatitude(), is(GOOGLE_LAT));
        assertThat(location.getLongitude(), is(GOOGLE_LNG));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressNotFoundTest() {
        doReturn(LOCATION_NOT_FOUND).when(locationService).get(any());

        Location location = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(LocationNotFoundException.class));
                    return Mono.empty();
                }).block();

        assertThat(location, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressExceptionTest() {
        doReturn(LOCATION_EXCEPTION).when(locationService).get(any());

        Location location = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(location, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressEmptyTest() {
        doReturn(LOCATION_EMPTY).when(locationService).get(any());

        Location location = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(location, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }

    @Test
    void fromAddressWrongStatusTest() {
        doReturn(LOCATION_WRONG_STATUS).when(locationService).get(any());

        Location location = GOOGLE_ADDRESS_MONO.transform(locationService::fromAddress)
                .onErrorResume(throwable -> {
                    assertThat(throwable, instanceOf(GetLocationException.class));
                    return Mono.empty();
                }).block();

        assertThat(location, is(nullValue()));

        verify(locationService, times(1)).fromAddress(any());
        verify(locationService, times(1)).buildUrl(any());
        verify(locationService, times(1)).get(any());
        verify(locationService, times(1)).geometryLocation(any());

        reset(locationService);
    }
}
