package com.renatmirzoev.moviebookingservice.exception.alreadyexists;

public class ActorAlreadyExistsException extends AbstractAlreadyExistsException {

    public ActorAlreadyExistsException(String message) {
        super(message);
    }
}
