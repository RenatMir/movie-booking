package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import com.renatmirzoev.moviebookingservice.repository.db.TheaterRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.CreateAuditoriumRequest;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.CreateAuditoriumResponse;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.GetAuditoriumResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

class AuditoriumControllerTest extends AbstractRestTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    private long theaterId;

    @BeforeEach
    @Transactional
    void init() {
        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        theaterId = theater.getId();
    }

    @Test
    void shouldGetAuditoriumNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Auditorium.GET, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetAuditorium() {
        CreateAuditoriumRequest createRequest = ModelUtils.createAuditoriumRequest();
        createRequest.setTheaterId(theaterId);
        ResponseEntity<CreateAuditoriumResponse> createResponse = performRequest(Endpoints.Auditorium.CREATE, CreateAuditoriumResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateAuditoriumResponse createAuditoriumResponse = createResponse.getBody();
        assertThat(createAuditoriumResponse).isNotNull();

        {
            ResponseEntity<GetAuditoriumResponse> getResponse = performRequest(Endpoints.Auditorium.GET, GetAuditoriumResponse.class)
                .pathParams(Map.of("id", String.valueOf(createAuditoriumResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetAuditoriumResponse getAuditoriumResponse = getResponse.getBody();
            assertThat(getAuditoriumResponse).isNotNull();
            assertRowsEqual(getAuditoriumResponse.getRows(), createRequest.getRows());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetAuditoriumResponse> getResponse = performRequest(location.toString(), GetAuditoriumResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetAuditoriumResponse getAuditoriumResponse = getResponse.getBody();
            assertThat(getAuditoriumResponse).isNotNull();
            assertRowsEqual(getAuditoriumResponse.getRows(), createRequest.getRows());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingAuditorium() {
        CreateAuditoriumRequest request = ModelUtils.createAuditoriumRequest();
        request.setTheaterId(theaterId);
        ResponseEntity<CreateAuditoriumResponse> createAuditoriumResponse1 = performRequest(Endpoints.Auditorium.CREATE, CreateAuditoriumResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createAuditoriumResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateAuditoriumResponse createAuditoriumResponse = createAuditoriumResponse1.getBody();
        assertThat(createAuditoriumResponse).isNotNull();

        ResponseEntity<ErrorResponse> createAuditoriumResponse2 = performRequest(Endpoints.Auditorium.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createAuditoriumResponse2, HttpStatus.BAD_REQUEST);
    }

    private void assertRowsEqual(SortedSet<GetAuditoriumResponse.Row> actual, SortedSet<CreateAuditoriumRequest.Row> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        assertThat(actual)
            .extracting(GetAuditoriumResponse.Row::getLabel)
            .containsExactlyElementsOf(
                expected.stream()
                    .map(CreateAuditoriumRequest.Row::getLabel)
                    .toList()
            );

        assertThat(actual)
            .zipSatisfy(expected, (actualRow, expectedRow) -> assertSeatsEqual(actualRow.getSeats(), expectedRow.getSeats()));
    }

    private void assertSeatsEqual(SortedSet<GetAuditoriumResponse.Row.Seat> actual, SortedSet<CreateAuditoriumRequest.Row.Seat> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        assertThat(actual)
            .extracting(GetAuditoriumResponse.Row.Seat::getLabel)
            .containsExactlyElementsOf(
                expected.stream()
                    .map(CreateAuditoriumRequest.Row.Seat::getLabel)
                    .toList()
            );
    }
}
