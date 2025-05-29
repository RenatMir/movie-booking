package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.repository.db.ActorRepository;
import com.renatmirzoev.moviebookingservice.repository.db.GenreRepository;
import com.renatmirzoev.moviebookingservice.rest.AbstractRestTest;
import com.renatmirzoev.moviebookingservice.rest.Endpoints;
import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.actor.ActorPublic;
import com.renatmirzoev.moviebookingservice.rest.model.genre.GenrePublic;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieRequest;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieResponse;
import com.renatmirzoev.moviebookingservice.rest.model.movie.GetMovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MovieControllerTest extends AbstractRestTest {

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActorRepository actorRepository;

    private Genre genre;
    private Actor actor;

    @Transactional
    @BeforeEach
    void init() {
        genre = genreRepository.save(ModelUtils.genre());
        actor = actorRepository.save(ModelUtils.actor());
    }

    @Test
    void shouldGetMovieNotFound() {
        ResponseEntity<ErrorResponse> response = performRequest(Endpoints.Movie.GET_BY_ID, ErrorResponse.class)
            .pathParams(Map.of("id", String.valueOf(Long.MAX_VALUE)))
            .andReturn();

        assertErrorResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndGetMovie() {
        CreateMovieRequest createRequest = ModelUtils.createMovieRequest();
        Set<Long> actorIds = Set.of(actor.getId());
        Set<Long> genreIds = Set.of(genre.getId());
        createRequest.setActorIds(actorIds);
        createRequest.setGenreIds(genreIds);

        ResponseEntity<CreateMovieResponse> createResponse = performRequest(Endpoints.Movie.CREATE, CreateMovieResponse.class)
            .httpMethod(HttpMethod.POST)
            .payload(createRequest)
            .andReturn();

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CreateMovieResponse createMovieResponse = createResponse.getBody();
        assertThat(createMovieResponse).isNotNull();

        {
            ResponseEntity<GetMovieResponse> getResponse = performRequest(Endpoints.Movie.GET_BY_ID, GetMovieResponse.class)
                .pathParams(Map.of("id", String.valueOf(createMovieResponse.getId())))
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetMovieResponse getMovieResponse = getResponse.getBody();
            assertThat(getMovieResponse).isNotNull();
            assertThat(getMovieResponse.getId()).isEqualTo(createMovieResponse.getId());
            assertThat(getMovieResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getMovieResponse.getDescription()).isEqualTo(createRequest.getDescription());

            assertThat(getMovieResponse.getActors())
                .extracting(ActorPublic::getId)
                .containsExactlyInAnyOrderElementsOf(actorIds);

            assertThat(getMovieResponse.getGenres())
                .extracting(GenrePublic::getId)
                .containsExactlyInAnyOrderElementsOf(genreIds);
        }

        {
            URI location = createResponse.getHeaders().getLocation();
            ResponseEntity<GetMovieResponse> getResponse = performRequest(location.toString(), GetMovieResponse.class)
                .andReturn();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            GetMovieResponse getMovieResponse = getResponse.getBody();
            assertThat(getMovieResponse).isNotNull();
            assertThat(getMovieResponse.getId()).isEqualTo(createMovieResponse.getId());
            assertThat(getMovieResponse.getName()).isEqualTo(createRequest.getName());
            assertThat(getMovieResponse.getDescription()).isEqualTo(createRequest.getDescription());

            assertThat(getMovieResponse.getActors())
                .extracting(ActorPublic::getId)
                .containsExactlyInAnyOrderElementsOf(actorIds);

            assertThat(getMovieResponse.getGenres())
                .extracting(GenrePublic::getId)
                .containsExactlyInAnyOrderElementsOf(genreIds);
        }
    }
}