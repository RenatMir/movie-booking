package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MovieCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private MovieCacheRepository movieCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Movie> movieOptional = movieCacheRepository.getById(Long.MAX_VALUE);
        assertThat(movieOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetMovieById() {
        Movie movie = ModelUtils.movie();
        movieCacheRepository.save(movie);

        Optional<Movie> movieOptional = movieCacheRepository.getById(movie.getId());
        assertThat(movieOptional).isPresent().contains(movie);
    }
}