package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class MovieAlreadyExistsException extends AbstractAlreadyExistsException {

    public MovieAlreadyExistsException(String message) {
        super(message);
    }
}
