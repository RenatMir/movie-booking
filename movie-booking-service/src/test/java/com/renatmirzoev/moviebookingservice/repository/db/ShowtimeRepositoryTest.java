package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class ShowtimeRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private AuditoriumRepository auditoriumRepository;

    private long movieId;
    private long auditoriumId;

    @BeforeEach
    @Transactional
    void init() {
        movieId = movieRepository.save(ModelUtils.movie());

        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        auditoriumId = auditoriumRepository.save(ModelUtils.auditorium().setTheaterId(theater.getId()));
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Showtime> showtimeOptional = showtimeRepository.getById(Long.MAX_VALUE);
        assertThat(showtimeOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetShowtimeById() {
        Showtime showtime = ModelUtils.showtime();
        showtime.setMovieId(movieId);
        showtime.setAuditoriumId(auditoriumId);
        showtime = showtimeRepository.save(showtime);

        Optional<Showtime> showtimeOptional = showtimeRepository.getById(showtime.getId());

        assertThat(showtimeOptional).isPresent().contains(showtime);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        Showtime showtime = ModelUtils.showtime();
        showtime.setMovieId(movieId);
        showtime.setAuditoriumId(auditoriumId);
        showtimeRepository.save(showtime);

        assertThatException()
            .isThrownBy(() -> showtimeRepository.save(showtime))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenShowtimeDoesNotExist() {
        boolean showtimeExists = showtimeRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE, Instant.now());
        assertThat(showtimeExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenShowtimeExists() {
        Showtime showtime = ModelUtils.showtime();
        showtime.setMovieId(movieId);
        showtime.setAuditoriumId(auditoriumId);
        showtime = showtimeRepository.save(showtime);

        boolean showtimeExists = showtimeRepository.exists(showtime.getMovieId(), showtime.getAuditoriumId(), showtime.getDateShow());
        assertThat(showtimeExists).isTrue();
    }

    @Test
    void shouldReturnTrueWhenShowtimeExistsTruncatedToMinutes() {
        Instant dateShow = Instant.now();
        Instant dateShowTruncated = dateShow.truncatedTo(ChronoUnit.MINUTES);
        Showtime showtime = ModelUtils.showtime();
        showtime.setDateShow(dateShow);
        showtime.setMovieId(movieId);
        showtime.setAuditoriumId(auditoriumId);
        showtime = showtimeRepository.save(showtime);

        boolean showtimeExists = showtimeRepository.exists(showtime.getMovieId(), showtime.getAuditoriumId(), dateShowTruncated);
        assertThat(showtimeExists).isTrue();
    }

}