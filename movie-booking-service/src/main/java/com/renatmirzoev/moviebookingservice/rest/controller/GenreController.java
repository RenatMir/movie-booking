package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.GenreNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.GenreMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreRequest;
import com.renatmirzoev.moviebookingservice.rest.model.genre.CreateGenreResponse;
import com.renatmirzoev.moviebookingservice.rest.model.genre.GetGenreResponse;
import com.renatmirzoev.moviebookingservice.service.GenreService;
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
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetGenreResponse> getGenre(@PathVariable("id") long id) {
        Optional<Genre> genreOptional = genreService.getGenreById(id);
        if (genreOptional.isEmpty()) {
            throw new GenreNotFoundException("Genre with id %s not found".formatted(id));
        }

        Genre genre = genreOptional.get();
        GetGenreResponse response = genreMapper.toGetGenreResponse(genre);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateGenreResponse> createGenre(@Valid @RequestBody CreateGenreRequest request) {
        Genre genre = genreMapper.toGenre(request);
        long id = genreService.saveGenre(genre);

        CreateGenreResponse response = new CreateGenreResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
