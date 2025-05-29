package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class AbstractAlreadyExistsException extends RuntimeException {

    protected AbstractAlreadyExistsException(String message) {
        super(message);
    }
}
