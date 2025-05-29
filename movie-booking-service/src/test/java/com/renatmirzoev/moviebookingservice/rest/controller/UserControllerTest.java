package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserRequest;
import com.renatmirzoev.moviebookingservice.rest.model.user.CreateUserResponse;
import com.renatmirzoev.moviebookingservice.rest.model.user.GetUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
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
        CreateUserRequest createRequest = ModelUtils.createUserRequest();
        ResponseEntity<CreateUserResponse> createResponse = performRequest(Endpoints.User.CREATE, CreateUserResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateUserResponse createUserResponse = createResponse.getBody();
        assertThat(createUserResponse).isNotNull();

        {
            ResponseEntity<GetUserResponse> getResponse = performRequest(Endpoints.User.GET_BY_ID, GetUserResponse.class)
                .pathParams(Map.of("id", String.valueOf(createUserResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetUserResponse getUserResponse = getResponse.getBody();
            assertThat(getUserResponse).isNotNull();
            assertThat(getUserResponse.getId()).isEqualTo(createUserResponse.getId());
            assertThat(getUserResponse.getFullName()).isEqualTo(createRequest.getFullName());
            assertThat(getUserResponse.getEmail()).isEqualTo(createRequest.getEmail());
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetUserResponse> getResponse = performRequest(location.toString(), GetUserResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetUserResponse getUserResponse = getResponse.getBody();
            assertThat(getUserResponse).isNotNull();
            assertThat(getUserResponse.getId()).isEqualTo(createUserResponse.getId());
            assertThat(getUserResponse.getFullName()).isEqualTo(createRequest.getFullName());
            assertThat(getUserResponse.getEmail()).isEqualTo(createRequest.getEmail());
        }
    }

    @Test
    void shouldGetBadRequestWhenCreatingExistingUser() {
        CreateUserRequest request = ModelUtils.createUserRequest();
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
