package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MovieRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActorRepository actorRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Movie> movieOptional = movieRepository.getById(Long.MAX_VALUE);
        assertThat(movieOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetMovieByIdWithoutGenresAndActors() {
        Movie movie = ModelUtils.movie();
        movie.setActors(Collections.emptySet());
        movie.setGenres(Collections.emptySet());
        long id = movieRepository.save(movie);

        Optional<Movie> movieOptional = movieRepository.getById(id);

        assertThat(movieOptional).isPresent();
        Movie getMovie = movieOptional.get();
        assertThat(getMovie)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .isEqualTo(movie);
    }

    @Test
    void shouldSaveAndGetMovieById() {
        Genre genre = ModelUtils.genre();
        genre = genreRepository.save(genre);
        Actor actor = ModelUtils.actor();
        actor = actorRepository.save(actor);

        Movie movie = ModelUtils.movie();
        movie.setGenres(Set.of(genre));
        movie.setActors(Set.of(actor));
        long id = movieRepository.save(movie);
        movieRepository.addActorsToMovie(id, movie.getActors());
        movieRepository.addGenresToMovie(id, movie.getGenres());

        Optional<Movie> movieOptional = movieRepository.getById(id);
        assertThat(movieOptional).isPresent();

        Movie getMovie = movieOptional.get();
        assertThat(getMovie)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .isEqualTo(movie);
    }
}