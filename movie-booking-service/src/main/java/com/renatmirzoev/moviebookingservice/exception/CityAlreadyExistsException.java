package com.renatmirzoev.moviebookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CityAlreadyExistsException extends RuntimeException {

    public CityAlreadyExistsException(String message) {
        super(message);
    }
}
