package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class CityAlreadyExistsException extends AbstractAlreadyExistsException {

    public CityAlreadyExistsException(String message) {
        super(message);
    }
}
