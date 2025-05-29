package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class ShowtimeAlreadyExistsException extends AbstractAlreadyExistsException {

    public ShowtimeAlreadyExistsException(String message) {
        super(message);
    }
}
