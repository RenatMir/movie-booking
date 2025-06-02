package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import com.renatmirzoev.moviebookingservice.repository.db.BookingRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import com.renatmirzoev.moviebookingservice.repository.db.MovieRepository;
import com.renatmirzoev.moviebookingservice.repository.db.ShowtimeRepository;
import com.renatmirzoev.moviebookingservice.repository.db.TheaterRepository;
import com.renatmirzoev.moviebookingservice.repository.db.UserRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.booking.CreateBookingRequest;
import com.renatmirzoev.moviebookingservice.rest.model.booking.CreateBookingResponse;
import com.renatmirzoev.moviebookingservice.rest.model.booking.GetBookingResponse;
import com.renatmirzoev.moviebookingservice.service.AuditoriumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BookingControllerTest extends AbstractRestTest {

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
    void shouldGetBookingNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Booking.GET, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetBooking() {
        CreateBookingRequest createRequest = ModelUtils.createBookingRequest();
        createRequest.setSeatId(seatId);
        createRequest.setShowtimeId(showtimeId);
        createRequest.setUserId(userId);

        ResponseEntity<CreateBookingResponse> createResponse = performRequest(Endpoints.Booking.CREATE, CreateBookingResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateBookingResponse createBookingResponse = createResponse.getBody();
        assertThat(createBookingResponse).isNotNull();

        {
            ResponseEntity<GetBookingResponse> getResponse = performRequest(Endpoints.Booking.GET, GetBookingResponse.class)
                .pathParams(Map.of("id", String.valueOf(createBookingResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetBookingResponse getBookingResponse = getResponse.getBody();
            assertThat(getBookingResponse).isNotNull();
            assertThat(getBookingResponse.getId()).isEqualTo(createBookingResponse.getId());
            assertThat(getBookingResponse.getUserId()).isEqualTo(createRequest.getUserId());
            assertThat(getBookingResponse.getShowtimeId()).isEqualTo(createRequest.getShowtimeId());
            assertThat(getBookingResponse.getSeatId()).isEqualTo(createRequest.getSeatId());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetBookingResponse> getResponse = performRequest(location.toString(), GetBookingResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetBookingResponse getBookingResponse = getResponse.getBody();
            assertThat(getBookingResponse).isNotNull();
            assertThat(getBookingResponse.getId()).isEqualTo(createBookingResponse.getId());
            assertThat(getBookingResponse.getUserId()).isEqualTo(createRequest.getUserId());
            assertThat(getBookingResponse.getShowtimeId()).isEqualTo(createRequest.getShowtimeId());
            assertThat(getBookingResponse.getSeatId()).isEqualTo(createRequest.getSeatId());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingBooking() {
        CreateBookingRequest request = ModelUtils.createBookingRequest();
        request.setSeatId(seatId);
        request.setShowtimeId(showtimeId);
        request.setUserId(userId);

        ResponseEntity<CreateBookingResponse> createBookingResponse1 = performRequest(Endpoints.Booking.CREATE, CreateBookingResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createBookingResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateBookingResponse createBookingResponse = createBookingResponse1.getBody();
        assertThat(createBookingResponse).isNotNull();

        ResponseEntity<ErrorResponse> createBookingResponse2 = performRequest(Endpoints.Booking.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createBookingResponse2, HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldDeleteNonExistingBooking() {
        ResponseEntity<Void> deleteResponse = performRequest(Endpoints.Booking.DELETE, Void.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .httpMethod(HttpMethod.DELETE)
            .andReturn();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteBooking() {
        CreateBookingRequest request = ModelUtils.createBookingRequest();
        request.setSeatId(seatId);
        request.setShowtimeId(showtimeId);
        request.setUserId(userId);

        ResponseEntity<CreateBookingResponse> createBookingResponse = performRequest(Endpoints.Booking.CREATE, CreateBookingResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createBookingResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateBookingResponse responseBody = createBookingResponse.getBody();
        assertThat(responseBody).isNotNull();

        {
            ResponseEntity<Void> deleteResponse = performRequest(Endpoints.Booking.DELETE, Void.class)
                .pathParams(Map.of("id", String.valueOf(responseBody.getId())))
                .httpMethod(HttpMethod.DELETE)
                .andReturn();
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
        {
            ResponseEntity<ErrorResponse> getResponse = performRequest(Endpoints.Booking.GET, ErrorResponse.class)
                .pathParams(Map.of("id", String.valueOf(responseBody.getId())))
                .andReturn();
            assertErrorResponse(getResponse, HttpStatus.NOT_FOUND);
        }
    }
}
