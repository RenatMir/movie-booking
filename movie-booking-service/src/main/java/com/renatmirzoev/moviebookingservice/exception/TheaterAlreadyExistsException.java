package com.renatmirzoev.moviebookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TheaterAlreadyExistsException extends RuntimeException {

    public TheaterAlreadyExistsException(String message) {
        super(message);
    }
}
