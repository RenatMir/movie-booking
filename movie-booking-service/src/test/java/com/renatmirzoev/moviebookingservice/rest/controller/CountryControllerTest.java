package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryRequest;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CountryControllerTest extends AbstractRestTest {

    @Test
    void shouldGetCountryNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Country.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetCountry() {
        CreateCountryRequest request = ModelUtils.createCountryRequest();
        ResponseEntity<CreateCountryResponse> response = performRequest(Endpoints.Country.CREATE, CreateCountryResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateCountryResponse createCountryResponse = response.getBody();
        assertThat(createCountryResponse).isNotNull();
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingCountry() {
        CreateCountryRequest request = ModelUtils.createCountryRequest();
        ResponseEntity<CreateCountryResponse> createCountryResponse1 = performRequest(Endpoints.Country.CREATE, CreateCountryResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createCountryResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateCountryResponse createCountryResponse = createCountryResponse1.getBody();
        assertThat(createCountryResponse).isNotNull();

        ResponseEntity<ErrorResponse> createCountryResponse2 = performRequest(Endpoints.Country.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createCountryResponse2, HttpStatus.BAD_REQUEST);
    }
}
