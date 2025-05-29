package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.repository.cache.MovieCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieCacheRepository movieCacheRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public long saveMovie(Movie movie) {
        long movieId = movieRepository.save(movie);
        movieRepository.addGenresToMovie(movieId, movie.getGenres());
        movieRepository.addActorsToMovie(movieId, movie.getActors());
        movieCacheRepository.save(movie);
        return movieId;
    }

    public Optional<Movie> getMovieById(long id) {
        return movieCacheRepository.getById(id)
            .or(() -> {
                Optional<Movie> movieOptional = movieRepository.getById(id);
                movieOptional.ifPresent(movieCacheRepository::save);
                return movieOptional;
            });
    }
}
