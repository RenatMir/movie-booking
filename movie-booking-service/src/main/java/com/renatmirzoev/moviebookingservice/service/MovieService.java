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

    @Transactional
    public long saveMovie(Movie movie) {
        long id = movieRepository.save(movie);
        movieRepository.addGenresToMovie(id, movie.getGenres());
        movieRepository.addActorsToMovie(id, movie.getActors());
        movieCacheRepository.save(movie);
        return id;
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
