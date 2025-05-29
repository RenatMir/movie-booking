package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterRequest;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterResponse;
import com.renatmirzoev.moviebookingservice.rest.model.theater.GetTheaterResponse;
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

class TheaterControllerTest extends AbstractRestTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;

    private long cityId;

    @BeforeEach
    @Transactional
    void init() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        City city = ModelUtils.city();
        city.setCountryId(country.getId());
        city = cityRepository.save(city);

        cityId = city.getId();
    }

    @Test
    void shouldGetTheaterNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Theater.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetTheater() {
        CreateTheaterRequest createRequest = ModelUtils.createTheaterRequest();
        createRequest.setCityId(cityId);
        ResponseEntity<CreateTheaterResponse> createResponse = performRequest(Endpoints.Theater.CREATE, CreateTheaterResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateTheaterResponse createTheaterResponse = createResponse.getBody();
        assertThat(createTheaterResponse).isNotNull();

        {
            ResponseEntity<GetTheaterResponse> getResponse = performRequest(Endpoints.Theater.GET_BY_ID, GetTheaterResponse.class)
                .pathParams(Map.of("id", String.valueOf(createTheaterResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetTheaterResponse getTheaterResponse = getResponse.getBody();
            assertThat(getTheaterResponse).isNotNull();
            assertThat(getTheaterResponse.getId()).isEqualTo(createTheaterResponse.getId());
            assertThat(getTheaterResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getTheaterResponse.getCityId()).isEqualTo(createRequest.getCityId());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetTheaterResponse> getResponse = performRequest(location.toString(), GetTheaterResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetTheaterResponse getTheaterResponse = getResponse.getBody();
            assertThat(getTheaterResponse).isNotNull();
            assertThat(getTheaterResponse.getId()).isEqualTo(createTheaterResponse.getId());
            assertThat(getTheaterResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getTheaterResponse.getCityId()).isEqualTo(createRequest.getCityId());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingTheater() {
        CreateTheaterRequest request = ModelUtils.createTheaterRequest();
        request.setCityId(cityId);
        ResponseEntity<CreateTheaterResponse> createTheaterResponse1 = performRequest(Endpoints.Theater.CREATE, CreateTheaterResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createTheaterResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateTheaterResponse createTheaterResponse = createTheaterResponse1.getBody();
        assertThat(createTheaterResponse).isNotNull();

        ResponseEntity<ErrorResponse> createTheaterResponse2 = performRequest(Endpoints.Theater.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createTheaterResponse2, HttpStatus.BAD_REQUEST);
    }
}
