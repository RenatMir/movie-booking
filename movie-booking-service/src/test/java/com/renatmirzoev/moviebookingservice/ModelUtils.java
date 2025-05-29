package com.renatmirzoev.moviebookingservice;

import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.model.entity.Seat;
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
            .ignore(field(User::getId))
            .ignore(field(User::getDateCreated))
            .create();
    }

    public static CreateUserRequest createUserRequest() {
        return Instancio.of(CreateUserRequest.class)
            .generate(field(CreateUserRequest::getEmail), gen -> gen.net().email())
            .create();
    }

    public static Country country() {
        return Instancio.of(Country.class)
            .ignore(field(Country::getId))
            .ignore(field(Country::getDateCreated))
            .create();
    }

    public static CreateCountryRequest createCountryRequest() {
        return Instancio.create(CreateCountryRequest.class);
    }

    public static City city() {
        return Instancio.of(City.class)
            .ignore(field(City::getId))
            .ignore(field(City::getDateCreated))
            .create();
    }

    public static CreateCityRequest createCityRequest() {
        return Instancio.create(CreateCityRequest.class);
    }

    public static Theater theater() {
        return Instancio.of(Theater.class)
            .ignore(field(Theater::getId))
            .ignore(field(Theater::getDateCreated))
            .create();
    }

    public static CreateTheaterRequest createTheaterRequest() {
        return Instancio.create(CreateTheaterRequest.class);
    }

    public static Genre genre() {
        return Instancio.of(Genre.class)
            .ignore(field(Genre::getId))
            .ignore(field(Genre::getDateCreated))
            .create();
    }

    public static CreateGenreRequest createGenreRequest() {
        return Instancio.create(CreateGenreRequest.class);
    }

    public static Actor actor() {
        return Instancio.of(Actor.class)
            .ignore(field(Actor::getId))
            .ignore(field(Actor::getDateCreated))
            .create();
    }

    public static CreateActorRequest createActorRequest() {
        return Instancio.create(CreateActorRequest.class);
    }

    public static Movie movie() {
        return Instancio.of(Movie.class)
            .ignore(field(Movie::getId))
            .ignore(field(Movie::getDateCreated))
            .create();
    }

    public static CreateMovieRequest createMovieRequest() {
        return Instancio.create(CreateMovieRequest.class);
    }

    public static Showtime showtime() {
        Showtime showtime = Instancio.of(Showtime.class)
            .ignore(field(Showtime::getId))
            .ignore(field(Showtime::getDateCreated))
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

    public static Auditorium auditorium() {
        return Instancio.of(Auditorium.class)
            .ignore(field(Auditorium::getId))
            .ignore(field(Auditorium::getDateCreated))
            .create();
    }

    public static Row row() {
        return Instancio.of(Row.class)
            .ignore(field(Row::getId))
            .ignore(field(Row::getDateCreated))
            .create();
    }

    public static Seat seat() {
        return Instancio.of(Seat.class)
            .ignore(field(Seat::getId))
            .ignore(field(Seat::getDateCreated))
            .create();
    }
}
