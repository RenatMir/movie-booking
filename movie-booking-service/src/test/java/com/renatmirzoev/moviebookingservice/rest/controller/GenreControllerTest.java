package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreRequest;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreResponse;
import com.renatmirzoev.moviebookingservice.rest.model.genre.GetGenreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class GenreControllerTest extends AbstractRestTest {

    @Test
    void shouldGetGenreNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Genre.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetGenre() {
        CreateGenreRequest createRequest = ModelUtils.createGenreRequest();
        ResponseEntity<CreateGenreResponse> createResponse = performRequest(Endpoints.Genre.CREATE, CreateGenreResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateGenreResponse createGenreResponse = createResponse.getBody();
        assertThat(createGenreResponse).isNotNull();

        {
            ResponseEntity<GetGenreResponse> getResponse = performRequest(Endpoints.Genre.GET_BY_ID, GetGenreResponse.class)
                .pathParams(Map.of("id", String.valueOf(createGenreResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetGenreResponse getGenreResponse = getResponse.getBody();
            assertThat(getGenreResponse).isNotNull();
            assertThat(getGenreResponse.getId()).isEqualTo(createGenreResponse.getId());
            assertThat(getGenreResponse.getName()).isEqualTo(createRequest.getName());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetGenreResponse> getResponse = performRequest(location.toString(), GetGenreResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetGenreResponse getGenreResponse = getResponse.getBody();
            assertThat(getGenreResponse).isNotNull();
            assertThat(getGenreResponse.getId()).isEqualTo(createGenreResponse.getId());
            assertThat(getGenreResponse.getName()).isEqualTo(createRequest.getName());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingGenre() {
        CreateGenreRequest request = ModelUtils.createGenreRequest();
        ResponseEntity<CreateGenreResponse> createGenreResponse1 = performRequest(Endpoints.Genre.CREATE, CreateGenreResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createGenreResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateGenreResponse createGenreResponse = createGenreResponse1.getBody();
        assertThat(createGenreResponse).isNotNull();

        ResponseEntity<ErrorResponse> createGenreResponse2 = performRequest(Endpoints.Genre.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createGenreResponse2, HttpStatus.BAD_REQUEST);
    }
}
