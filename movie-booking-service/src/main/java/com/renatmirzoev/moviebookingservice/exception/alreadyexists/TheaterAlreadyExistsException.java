package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class TheaterAlreadyExistsException extends AbstractAlreadyExistsException {

    public TheaterAlreadyExistsException(String message) {
        super(message);
    }
}
