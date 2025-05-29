package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.repository.cache.MovieCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieCacheRepository movieCacheRepository;
    @InjectMocks
    private MovieService movieService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(movieRepository, movieCacheRepository);
    }

    @Test
    void shouldSaveMovie() {
        Movie movie = ModelUtils.movie();

        when(movieRepository.save(any(Movie.class))).thenReturn(Long.MAX_VALUE);

        movieService.saveMovie(movie);

        inOrder.verify(movieRepository).save(any(Movie.class));
        inOrder.verify(movieRepository).addGenresToMovie(anyLong(), anySet());
        inOrder.verify(movieRepository).addActorsToMovie(anyLong(), anySet());
        inOrder.verify(movieCacheRepository).save(any(Movie.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyMovieById() {
        when(movieCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(movieRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Movie> movieOptional = movieService.getMovieById(Long.MAX_VALUE);
        assertThat(movieOptional).isEmpty();

        inOrder.verify(movieCacheRepository).getById(anyLong());
        inOrder.verify(movieRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetMovieByIdFromCache() {
        Movie movie = ModelUtils.movie();

        when(movieCacheRepository.getById(anyLong())).thenReturn(Optional.of(movie));

        Optional<Movie> movieOptional = movieService.getMovieById(Long.MAX_VALUE);
        assertThat(movieOptional).isPresent().contains(movie);

        inOrder.verify(movieCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetMovieByIdFromDB() {
        Movie movie = ModelUtils.movie();

        when(movieCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(movieRepository.getById(anyLong())).thenReturn(Optional.of(movie));

        Optional<Movie> movieOptional = movieService.getMovieById(Long.MAX_VALUE);
        assertThat(movieOptional).isPresent().contains(movie);

        inOrder.verify(movieCacheRepository).getById(anyLong());
        inOrder.verify(movieRepository).getById(anyLong());
        inOrder.verify(movieCacheRepository).save(any(Movie.class));
        inOrder.verifyNoMoreInteractions();
    }
}