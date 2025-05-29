package com.renatmirzoev.moviebookingservice;

import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityRequest;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public class ModelUtils {

    private ModelUtils() {
    }

    public static User user() {
        return Instancio.of(User.class)
            .generate(field(User::getEmail), gen -> gen.net().email())
            .create();
    }

    public static CreateUserRequest createUserRequest() {
        return Instancio.of(CreateUserRequest.class)
            .generate(field(CreateUserRequest::getEmail), gen -> gen.net().email())
            .create();
    }

    public static Country country() {
        return Instancio.create(Country.class);
    }

    public static CreateCountryRequest createCountryRequest() {
        return Instancio.create(CreateCountryRequest.class);
    }

    public static City city() {
        return Instancio.create(City.class);
    }

    public static CreateCityRequest createCityRequest() {
        return Instancio.create(CreateCityRequest.class);
    }
}
