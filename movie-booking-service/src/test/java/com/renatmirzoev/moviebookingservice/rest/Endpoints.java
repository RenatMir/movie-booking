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
        GET_BY_ID("/users/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Country implements Endpoint {
        CREATE("/countries"),
        GET_BY_ID("/countries/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum City implements Endpoint {
        CREATE("/cities"),
        GET_BY_ID("/cities/{id}");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Theater implements Endpoint {
        CREATE("/theaters"),
        GET_BY_ID("/theaters/{id}");

        private final String path;
    }
}
