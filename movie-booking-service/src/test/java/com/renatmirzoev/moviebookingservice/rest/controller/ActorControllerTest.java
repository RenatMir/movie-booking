package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorRequest;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.actor.GetActorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ActorControllerTest extends AbstractRestTest {

    @Test
    void shouldGetActorNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Actor.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetActor() {
        CreateActorRequest createRequest = ModelUtils.createActorRequest();
        ResponseEntity<CreateActorResponse> createResponse = performRequest(Endpoints.Actor.CREATE, CreateActorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateActorResponse createActorResponse = createResponse.getBody();
        assertThat(createActorResponse).isNotNull();

        {
            ResponseEntity<GetActorResponse> getResponse = performRequest(Endpoints.Actor.GET_BY_ID, GetActorResponse.class)
                .pathParams(Map.of("id", String.valueOf(createActorResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetActorResponse getActorResponse = getResponse.getBody();
            assertThat(getActorResponse).isNotNull();
            assertThat(getActorResponse.getId()).isEqualTo(createActorResponse.getId());
            assertThat(getActorResponse.getFullName()).isEqualTo(createRequest.getFullName());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetActorResponse> getResponse = performRequest(location.toString(), GetActorResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetActorResponse getActorResponse = getResponse.getBody();
            assertThat(getActorResponse).isNotNull();
            assertThat(getActorResponse.getId()).isEqualTo(createActorResponse.getId());
            assertThat(getActorResponse.getFullName()).isEqualTo(createRequest.getFullName());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingActor() {
        CreateActorRequest request = ModelUtils.createActorRequest();
        ResponseEntity<CreateActorResponse> createActorResponse1 = performRequest(Endpoints.Actor.CREATE, CreateActorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createActorResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateActorResponse createActorResponse = createActorResponse1.getBody();
        assertThat(createActorResponse).isNotNull();

        ResponseEntity<ErrorResponse> createActorResponse2 = performRequest(Endpoints.Actor.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createActorResponse2, HttpStatus.BAD_REQUEST);
    }
}
