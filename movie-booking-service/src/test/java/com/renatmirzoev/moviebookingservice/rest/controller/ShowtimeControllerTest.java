package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import com.renatmirzoev.moviebookingservice.repository.db.MovieRepository;
import com.renatmirzoev.moviebookingservice.repository.db.TheaterRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeRequest;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeResponse;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.GetShowtimeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ShowtimeControllerTest extends AbstractRestTest {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    private long movieId;
    private long theaterId;

    @BeforeEach
    @Transactional
    void init() {
        movieId = movieRepository.save(ModelUtils.movie());

        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        theaterId = theater.getId();
    }

    @Test
    void shouldGetShowtimeNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Showtime.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetShowtime() {
        CreateShowtimeRequest createRequest = ModelUtils.createShowtimeRequest();
        createRequest.setMovieId(movieId);
        createRequest.setTheaterId(theaterId);
        ResponseEntity<CreateShowtimeResponse> createResponse = performRequest(Endpoints.Showtime.CREATE, CreateShowtimeResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateShowtimeResponse createShowtimeResponse = createResponse.getBody();
        assertThat(createShowtimeResponse).isNotNull();

        {
            ResponseEntity<GetShowtimeResponse> getResponse = performRequest(Endpoints.Showtime.GET_BY_ID, GetShowtimeResponse.class)
                .pathParams(Map.of("id", String.valueOf(createShowtimeResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetShowtimeResponse getShowtimeResponse = getResponse.getBody();
            assertThat(getShowtimeResponse).isNotNull();
            assertThat(getShowtimeResponse.getId()).isEqualTo(createShowtimeResponse.getId());
            assertThat(getShowtimeResponse.getMovieId()).isEqualTo(createRequest.getMovieId());
            assertThat(getShowtimeResponse.getTheaterId()).isEqualTo(createRequest.getTheaterId());
            assertThat(getShowtimeResponse.getDateShow()).isEqualTo(createRequest.getDateShow());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetShowtimeResponse> getResponse = performRequest(location.toString(), GetShowtimeResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetShowtimeResponse getShowtimeResponse = getResponse.getBody();
            assertThat(getShowtimeResponse).isNotNull();
            assertThat(getShowtimeResponse.getId()).isEqualTo(createShowtimeResponse.getId());
            assertThat(getShowtimeResponse.getMovieId()).isEqualTo(createRequest.getMovieId());
            assertThat(getShowtimeResponse.getTheaterId()).isEqualTo(createRequest.getTheaterId());
            assertThat(getShowtimeResponse.getDateShow()).isEqualTo(createRequest.getDateShow());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingShowtime() {
        CreateShowtimeRequest request = ModelUtils.createShowtimeRequest();
        request.setMovieId(movieId);
        request.setTheaterId(theaterId);
        ResponseEntity<CreateShowtimeResponse> createShowtimeResponse1 = performRequest(Endpoints.Showtime.CREATE, CreateShowtimeResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createShowtimeResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateShowtimeResponse createShowtimeResponse = createShowtimeResponse1.getBody();
        assertThat(createShowtimeResponse).isNotNull();

        ResponseEntity<ErrorResponse> createShowtimeResponse2 = performRequest(Endpoints.Showtime.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createShowtimeResponse2, HttpStatus.BAD_REQUEST);
    }
}
