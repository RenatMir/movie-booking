package com.renatmirzoev.moviebookingservice;

import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public class ModelUtils {

    private ModelUtils() {
    }

    public static User createUser() {
        return Instancio.of(User.class)
            .generate(field(User::getEmail), gen -> gen.net().email())
            .create();
    }

    public static CreateUserRequest createCreateUserRequest() {
        return Instancio.of(CreateUserRequest.class)
            .generate(field(CreateUserRequest::getEmail), gen -> gen.net().email())
            .create();
    }
}
