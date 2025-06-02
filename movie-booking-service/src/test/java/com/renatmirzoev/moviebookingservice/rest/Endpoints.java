package com.renatmirzoev.moviebookingservice.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Endpoints {

    public interface Endpoint {
        String getPath();
    }

    @Getter
    @RequiredArgsConstructor
    public enum User implements Endpoint {
        CREATE("/users"),
        GET("/users/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Country implements Endpoint {
        CREATE("/countries"),
        GET("/countries/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum City implements Endpoint {
        CREATE("/cities"),
        GET("/cities/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Theater implements Endpoint {
        CREATE("/theaters"),
        GET("/theaters/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Genre implements Endpoint {
        CREATE("/genres"),
        GET("/genres/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Actor implements Endpoint {
        CREATE("/actors"),
        GET("/actors/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Movie implements Endpoint {
        CREATE("/movies"),
        GET("/movies/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Showtime implements Endpoint {
        CREATE("/showtimes"),
        GET("/showtimes/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Auditorium implements Endpoint {
        CREATE("/auditoriums"),
        GET("/auditoriums/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Booking implements Endpoint {
        CREATE("/bookings"),
        GET("/bookings/{id}"),
        DELETE("/bookings/{id}");

        private final String path;
    }
}
