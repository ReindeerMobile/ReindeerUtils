
package com.reindeermobile.mvpexample.entities;

import java.util.Arrays;

public class Result {
    private Geometry geometry;
    private String icon;
    private String id;
    private String name;
    private String reference;
    private String[] types;
    private String vicinity;

    public class Geometry {
        private Location location;

        public class Location {
            private double lat;
            private double lng;

            public final double getLat() {
                return this.lat;
            }

            public final double getLng() {
                return this.lng;
            }

            @Override
            public String toString() {
                return "Location [lat=" + this.lat + ", lng=" + this.lng + "]";
            }
        }

        public final Location getLocation() {
            return this.location;
        }

        @Override
        public String toString() {
            return "Geometry [location=" + this.location + "]";
        }

    }

    public final Geometry getGeometry() {
        return this.geometry;
    }

    public final String getIcon() {
        return this.icon;
    }

    public final String getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final String getReference() {
        return this.reference;
    }

    public final String[] getTypes() {
        return this.types;
    }

    public final String getVicinity() {
        return this.vicinity;
    }

    @Override
    public String toString() {
        return "Result [geometry=" + this.geometry + ", icon=" + this.icon + ", id=" + this.id
                + ", name=" + this.name + ", reference=" + this.reference + ", types="
                + Arrays.toString(this.types) + ", vicinity=" + this.vicinity + "]";
    }

}
