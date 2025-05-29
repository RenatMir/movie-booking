package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.alreadyexists.GenreAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.repository.cache.GenreCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreCacheRepository genreCacheRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public long save(Genre genre) {
        if (genreExists(genre.getName())) {
            throw new GenreAlreadyExistsException("Genre with name %s already exists".formatted(genre.getName()));
        }

        Genre savedGenre = genreRepository.save(genre);
        genreCacheRepository.save(savedGenre);
        return savedGenre.getId();
    }

    public Optional<Genre> getGenreById(long id) {
        return genreCacheRepository.getById(id)
            .or(() -> {
                Optional<Genre> genreOptional = genreRepository.getById(id);
                genreOptional.ifPresent(genreCacheRepository::save);
                return genreOptional;
            });
    }

    public boolean genreExists(String name) {
        return genreCacheRepository.exists(name)
            .orElseGet(() -> {
                boolean value = genreRepository.exists(name);
                genreCacheRepository.saveExists(name);
                return value;
            });
    }

}
