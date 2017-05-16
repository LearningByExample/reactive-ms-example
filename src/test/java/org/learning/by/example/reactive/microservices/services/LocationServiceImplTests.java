package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.Location;
import org.learning.by.example.reactive.microservices.model.LocationResult;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;


@UnitTest
@DisplayName("LocationServiceImpl Unit Tests")
class LocationServiceImplTests {

    /*String[] types = {"a"};
LocationResult.Result.Address_component address_component = new LocationResult.Result.Address_component("a","a", types);
LocationResult.Result.Address_component[] address_components = {address_component};
LocationResult.Result.Geometry.Bounds.Northeast northeast = new LocationResult.Result.Geometry.Bounds.Northeast(100,100);
LocationResult.Result.Geometry.Bounds.Southwest southwest = new LocationResult.Result.Geometry.Bounds.Southwest(100,100);
LocationResult.Result.Geometry.Bounds bounds = new LocationResult.Result.Geometry.Bounds(northeast,southwest);
LocationResult.Result.Geometry.Location location = new LocationResult.Result.Geometry.Location(100,100);
LocationResult.Result.Geometry.Viewport.Northeast northeastV = new LocationResult.Result.Geometry.Viewport.Northeast(100,100);
LocationResult.Result.Geometry.Viewport.Southwest southwestV = new LocationResult.Result.Geometry.Viewport.Southwest(100,100);
LocationResult.Result.Geometry.Viewport viewport = new LocationResult.Result.Geometry.Viewport(northeastV,southwestV);
LocationResult.Result.Geometry geometry = new LocationResult.Result.Geometry(bounds,location,"a",viewport);
LocationResult.Result result = new LocationResult.Result(address_components,"a", geometry, "a", types);
LocationResult.Result[] results = {result};
LocationResult locationResult = new LocationResult(results, "OK");

return Mono.just(locationResult);*/

    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final String BAD_ADDRESS = "bad address";
    private static final Mono<String> GOOGLE_ADDRESS_MONO = Mono.just(GOOGLE_ADDRESS);
    private static final Mono<String> BAD_ADDRESS_MONO = Mono.just(BAD_ADDRESS);
    private static final String OK_STATUS = "OK";
    private static final String ZERO_RESULTS = "ZERO_RESULTS";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;

    @Autowired
    private LocationServiceImpl locationService;

    @Test
    void getBeamTest() {
        assertThat(locationService, is(notNullValue()));
    }

    @Test
    void requestTest(){
        LocationResult location = GOOGLE_ADDRESS_MONO.publish(locationService::request).block();

        assertThat(location, is(notNullValue()));
        assertThat(location.getStatus(), is(OK_STATUS));
    }

    @Test
    void requestNotFoundTest(){
        LocationResult location = BAD_ADDRESS_MONO.publish(locationService::request).block();

        assertThat(location, is(notNullValue()));
        assertThat(location.getStatus(), is(ZERO_RESULTS));
    }

    @Test
    void fromAddressTest() {
        Location location = GOOGLE_ADDRESS_MONO.publish(locationService::fromAddress).block();

        assertThat(location, is(notNullValue()));
        assertThat(location.getLat(), is(GOOGLE_LAT));
        assertThat(location.getLng(), is(GOOGLE_LNG));
    }

    @Test
    void fromAddressNotFoundTest() {
        Location location = BAD_ADDRESS_MONO.publish(locationService::fromAddress)
            .onErrorResume(throwable -> {
                assertThat(throwable, instanceOf(LocationNotFoundException.class));
                return Mono.empty();
            }).block();

        assertThat(location, is(nullValue()));
    }
}
