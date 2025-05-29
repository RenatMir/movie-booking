package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.MovieNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.MovieMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Movie;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieRequest;
import com.renatmirzoev.moviebookingservice.rest.model.movie.CreateMovieResponse;
import com.renatmirzoev.moviebookingservice.rest.model.movie.GetMovieResponse;
import com.renatmirzoev.moviebookingservice.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetMovieResponse> getMovieById(@PathVariable("id") long id) {
        Optional<Movie> movieOptional = movieService.getMovieById(id);
        if (movieOptional.isEmpty()) {
            throw new MovieNotFoundException("Movie with id %s not found".formatted(id));
        }

        Movie movie = movieOptional.get();
        GetMovieResponse response = movieMapper.toGetMovieResponse(movie);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateMovieResponse> createMovie(@Valid @RequestBody CreateMovieRequest request) {
        Movie movie = movieMapper.toMovie(request);
        long id = movieService.saveMovie(movie);

        CreateMovieResponse response = new CreateMovieResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
