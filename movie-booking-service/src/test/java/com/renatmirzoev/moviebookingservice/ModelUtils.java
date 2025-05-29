package com.renatmirzoev.moviebookingservice;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorRequest;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityRequest;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryRequest;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreRequest;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieRequest;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeRequest;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import org.instancio.Instancio;

import java.time.Instant;

import static com.renatmirzoev.moviebookingservice.model.entity.Showtime.DATE_SHOW_TRUNCATION;
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

    public static Theater theater() {
        return Instancio.create(Theater.class);
    }

    public static CreateTheaterRequest createTheaterRequest() {
        return Instancio.create(CreateTheaterRequest.class);
    }

    public static Genre genre() {
        return Instancio.create(Genre.class);
    }

    public static CreateGenreRequest createGenreRequest() {
        return Instancio.create(CreateGenreRequest.class);
    }

    public static Actor actor() {
        return Instancio.create(Actor.class);
    }

    public static CreateActorRequest createActorRequest() {
        return Instancio.create(CreateActorRequest.class);
    }

    public static Movie movie() {
        return Instancio.create(Movie.class);
    }

    public static CreateMovieRequest createMovieRequest() {
        return Instancio.create(CreateMovieRequest.class);
    }

    public static Showtime showtime() {
        Showtime showtime = Instancio.of(Showtime.class)
            .create();
        showtime.setDateShow(Instant.now().truncatedTo(DATE_SHOW_TRUNCATION));
        return showtime;
    }

    public static CreateShowtimeRequest createShowtimeRequest() {
        CreateShowtimeRequest createShowtimeRequest = Instancio.of(CreateShowtimeRequest.class)
            .create();
        createShowtimeRequest.setDateShow(Instant.now().truncatedTo(DATE_SHOW_TRUNCATION));
        return createShowtimeRequest;
    }
}
