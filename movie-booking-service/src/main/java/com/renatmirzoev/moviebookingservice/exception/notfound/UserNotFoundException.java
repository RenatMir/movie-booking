package com.renatmirzoev.moviebookingservice.exception.notfound;

public class UserNotFoundException extends AbstractNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
