package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityRequest;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityResponse;
import com.renatmirzoev.moviebookingservice.rest.model.city.GetCityResponse;
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

class CityControllerTest extends AbstractRestTest {

    @Autowired
    private CountryRepository countryRepository;

    private long countryId;

    @BeforeEach
    @Transactional
    void init() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        countryId = country.getId();
    }

    @Test
    void shouldGetCityNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.City.GET, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetCity() {
        CreateCityRequest createRequest = ModelUtils.createCityRequest();
        createRequest.setCountryId(countryId);
        ResponseEntity<CreateCityResponse> createResponse = performRequest(Endpoints.City.CREATE, CreateCityResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateCityResponse createCityResponse = createResponse.getBody();
        assertThat(createCityResponse).isNotNull();

        {
            ResponseEntity<GetCityResponse> getResponse = performRequest(Endpoints.City.GET, GetCityResponse.class)
                .pathParams(Map.of("id", String.valueOf(createCityResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetCityResponse getCityResponse = getResponse.getBody();
            assertThat(getCityResponse).isNotNull();
            assertThat(getCityResponse.getId()).isEqualTo(createCityResponse.getId());
            assertThat(getCityResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getCityResponse.getCountryId()).isEqualTo(createRequest.getCountryId());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetCityResponse> getResponse = performRequest(location.toString(), GetCityResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetCityResponse getCityResponse = getResponse.getBody();
            assertThat(getCityResponse).isNotNull();
            assertThat(getCityResponse.getId()).isEqualTo(createCityResponse.getId());
            assertThat(getCityResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getCityResponse.getCountryId()).isEqualTo(createRequest.getCountryId());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingCity() {
        CreateCityRequest request = ModelUtils.createCityRequest();
        request.setCountryId(countryId);
        ResponseEntity<CreateCityResponse> createCityResponse1 = performRequest(Endpoints.City.CREATE, CreateCityResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createCityResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateCityResponse createCityResponse = createCityResponse1.getBody();
        assertThat(createCityResponse).isNotNull();

        ResponseEntity<ErrorResponse> createCityResponse2 = performRequest(Endpoints.City.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createCityResponse2, HttpStatus.BAD_REQUEST);
    }
}
