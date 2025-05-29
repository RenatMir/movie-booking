package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class AuditoriumAlreadyExistsException extends AbstractAlreadyExistsException {

    public AuditoriumAlreadyExistsException(String message) {
        super(message);
    }
}
