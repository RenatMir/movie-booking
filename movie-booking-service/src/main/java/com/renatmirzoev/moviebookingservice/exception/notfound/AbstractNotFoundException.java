package com.renatmirzoev.moviebookingservice.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class AbstractNotFoundException extends RuntimeException {

    protected AbstractNotFoundException(String message) {
        super(message);
    }
}
