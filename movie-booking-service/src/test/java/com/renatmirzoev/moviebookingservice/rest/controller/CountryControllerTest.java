package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryRequest;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryResponse;
import com.renatmirzoev.moviebookingservice.rest.model.country.GetCountryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
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
        CreateCountryRequest createRequest = ModelUtils.createCountryRequest();
        ResponseEntity<CreateCountryResponse> createResponse = performRequest(Endpoints.Country.CREATE, CreateCountryResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateCountryResponse createCountryResponse = createResponse.getBody();
        assertThat(createCountryResponse).isNotNull();

        {
            ResponseEntity<GetCountryResponse> getResponse = performRequest(Endpoints.Country.GET_BY_ID, GetCountryResponse.class)
                .pathParams(Map.of("id", String.valueOf(createCountryResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetCountryResponse getCountryResponse = getResponse.getBody();
            assertThat(getCountryResponse).isNotNull();
            assertThat(getCountryResponse.getId()).isEqualTo(createCountryResponse.getId());
            assertThat(getCountryResponse.getName()).isEqualTo(createRequest.getName());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetCountryResponse> getResponse = performRequest(location.toString(), GetCountryResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetCountryResponse getCountryResponse = getResponse.getBody();
            assertThat(getCountryResponse).isNotNull();
            assertThat(getCountryResponse.getId()).isEqualTo(createCountryResponse.getId());
            assertThat(getCountryResponse.getName()).isEqualTo(createRequest.getName());
        }
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
