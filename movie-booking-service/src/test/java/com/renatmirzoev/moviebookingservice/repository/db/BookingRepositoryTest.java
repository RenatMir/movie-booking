package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.service.AuditoriumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class BookingRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;
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
    private AuditoriumService auditoriumService;
    @Autowired
    private UserRepository userRepository;

    private long userId;
    private long showtimeId;
    private long seatId;

    @BeforeEach
    @Transactional
    void init() {
        long movieId = movieRepository.save(ModelUtils.movie());
        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));

        Auditorium auditorium = ModelUtils.auditorium().setTheaterId(theater.getId());
        long auditoriumId = auditoriumService.saveAuditorium(auditorium);
        Optional<Auditorium> auditoriumOptional = auditoriumService.getAuditoriumById(auditoriumId);
        assertThat(auditoriumOptional).isPresent();
        auditorium = auditoriumOptional.get();
        seatId = auditorium.getRows().getFirst().getSeats().getFirst().getId();

        Showtime showtime = showtimeRepository.save(ModelUtils.showtime().setAuditoriumId(auditoriumId).setMovieId(movieId));
        showtimeId = showtime.getId();

        User user = userRepository.save(ModelUtils.user());
        userId = user.getId();
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Booking> bookingOptional = bookingRepository.getById(Long.MAX_VALUE);
        assertThat(bookingOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetById() {
        Booking booking = ModelUtils.booking();
        booking.setSeatId(seatId);
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        booking = bookingRepository.save(booking);

        Optional<Booking> bookingOptional = bookingRepository.getById(booking.getId());

        assertThat(bookingOptional).isPresent().contains(booking);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        Booking booking = ModelUtils.booking();
        booking.setSeatId(seatId);
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        bookingRepository.save(booking);

        assertThatException()
            .isThrownBy(() -> bookingRepository.save(booking))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenBookingDoesNotExist() {
        boolean exists = bookingRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenBookingExists() {
        Booking booking = ModelUtils.booking();
        booking.setSeatId(seatId);
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        bookingRepository.save(booking);

        boolean exists = bookingRepository.exists(showtimeId, seatId);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotDeleteNonExistingBooking() {
        Optional<Booking> deleted = bookingRepository.deleteById(Long.MAX_VALUE);
        assertThat(deleted).isEmpty();
    }

    @Test
    void shouldDeleteExistingBooking() {
        Booking booking = ModelUtils.booking();
        booking.setSeatId(seatId);
        booking.setUserId(userId);
        booking.setShowtimeId(showtimeId);
        booking = bookingRepository.save(booking);

        Optional<Booking> deleted = bookingRepository.deleteById(booking.getId());
        assertThat(deleted).isPresent().contains(booking);

        Optional<Booking> bookingOptional = bookingRepository.getById(booking.getId());
        assertThat(bookingOptional).isEmpty();
    }

}