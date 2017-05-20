package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeoLocationResponse {
    private final Result results[];
    private final String status;

    @JsonCreator
    public GeoLocationResponse(@JsonProperty("results") Result[] results, @JsonProperty("status") String status){
        this.results = results;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Result[] getResults() {
        return results;
    }

    public static final class Result {
        final Address_component address_components[];
        final String formatted_address;

        public Geometry getGeometry() {
            return geometry;
        }

        final Geometry geometry;
        final String place_id;
        final String[] types;

        @JsonCreator
        public Result(@JsonProperty("address_components") Address_component[] address_components, @JsonProperty("formatted_address") String formatted_address, @JsonProperty("geometry") Geometry geometry, @JsonProperty("place_id") String place_id, @JsonProperty("types") String[] types){
            this.address_components = address_components;
            this.formatted_address = formatted_address;
            this.geometry = geometry;
            this.place_id = place_id;
            this.types = types;
        }

        public static final class Address_component {
            final String long_name;
            final String short_name;
            final String[] types;

            @JsonCreator
            public Address_component(@JsonProperty("long_name") String long_name, @JsonProperty("short_name") String short_name, @JsonProperty("types") String[] types){
                this.long_name = long_name;
                this.short_name = short_name;
                this.types = types;
            }
        }

        public static final class Geometry {
            final Bounds bounds;

            public Location getLocation() {
                return location;
            }

            final Location location;
            final String location_type;
            final Viewport viewport;

            @JsonCreator
            public Geometry(@JsonProperty("bounds") Bounds bounds, @JsonProperty("location") Location location, @JsonProperty("location_type") String location_type, @JsonProperty("viewport") Viewport viewport){
                this.bounds = bounds;
                this.location = location;
                this.location_type = location_type;
                this.viewport = viewport;
            }

            public static final class Bounds {
                final Northeast northeast;
                final Southwest southwest;

                @JsonCreator
                public Bounds(@JsonProperty("northeast") Northeast northeast, @JsonProperty("southwest") Southwest southwest){
                    this.northeast = northeast;
                    this.southwest = southwest;
                }

                public static final class Northeast {
                    final double lat;
                    final double lng;

                    @JsonCreator
                    public Northeast(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
                        this.lat = lat;
                        this.lng = lng;
                    }
                }

                public static final class Southwest {
                    final double lat;
                    final double lng;

                    @JsonCreator
                    public Southwest(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
                        this.lat = lat;
                        this.lng = lng;
                    }
                }
            }

            public static final class Location {
                public double getLat() {
                    return lat;
                }

                public double getLng() {
                    return lng;
                }

                final double lat;
                final double lng;

                @JsonCreator
                public Location(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
                    this.lat = lat;
                    this.lng = lng;
                }
            }

            public static final class Viewport {
                final Northeast northeast;
                final Southwest southwest;

                @JsonCreator
                public Viewport(@JsonProperty("northeast") Northeast northeast, @JsonProperty("southwest") Southwest southwest){
                    this.northeast = northeast;
                    this.southwest = southwest;
                }

                public static final class Northeast {
                    final double lat;
                    final double lng;

                    @JsonCreator
                    public Northeast(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
                        this.lat = lat;
                        this.lng = lng;
                    }
                }

                public static final class Southwest {
                    final double lat;
                    final double lng;

                    @JsonCreator
                    public Southwest(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
                        this.lat = lat;
                        this.lng = lng;
                    }
                }
            }
        }
    }
}