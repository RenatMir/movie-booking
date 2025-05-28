package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends AbstractRestTest {

    @Test
    void shouldGetUserNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.User.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetUser() {
        CreateUserRequest request = ModelUtils.createCreateUserRequest();
        ResponseEntity<CreateUserResponse> response = performRequest(Endpoints.User.CREATE, CreateUserResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateUserResponse createUserResponse = response.getBody();
        assertThat(createUserResponse).isNotNull();
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingUser() {
        CreateUserRequest request = ModelUtils.createCreateUserRequest();
        ResponseEntity<CreateUserResponse> createUserResponse1 = performRequest(Endpoints.User.CREATE, CreateUserResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertThat(createUserResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateUserResponse createUserResponse = createUserResponse1.getBody();
        assertThat(createUserResponse).isNotNull();

        ResponseEntity<ErrorResponse> createUserResponse2 = performRequest(Endpoints.User.CREATE, ErrorResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(request)
            .andReturn();

        assertErrorResponse(createUserResponse2, HttpStatus.BAD_REQUEST);
    }
}
