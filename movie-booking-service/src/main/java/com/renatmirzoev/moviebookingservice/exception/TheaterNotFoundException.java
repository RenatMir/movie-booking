package com.renatmirzoev.moviebookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TheaterNotFoundException extends RuntimeException {

    public TheaterNotFoundException(String message) {
        super(message);
    }
}
