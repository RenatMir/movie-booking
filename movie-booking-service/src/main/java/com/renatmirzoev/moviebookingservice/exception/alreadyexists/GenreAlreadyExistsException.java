package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class GenreAlreadyExistsException extends AbstractAlreadyExistsException {

    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
