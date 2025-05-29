package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class CountryAlreadyExistsException extends AbstractAlreadyExistsException {

    public CountryAlreadyExistsException(String message) {
        super(message);
    }
}
