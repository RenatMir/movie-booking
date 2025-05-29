package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class GenreRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Genre> genreOptional = genreRepository.getById(Long.MAX_VALUE);
        assertThat(genreOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetGenreById() {
        Genre genre = ModelUtils.genre();
        genre = genreRepository.save(genre);

        Optional<Genre> genreOptional = genreRepository.getById(genre.getId());

        assertThat(genreOptional).isPresent().contains(genre);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        Genre genre = ModelUtils.genre();
        genreRepository.save(genre);

        assertThatException()
            .isThrownBy(() -> genreRepository.save(genre))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenGenreDoesNotExist() {
        boolean genreExists = genreRepository.exists(UUID.randomUUID().toString());
        assertThat(genreExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenGenreExists() {
        Genre genre = ModelUtils.genre();
        genre = genreRepository.save(genre);

        boolean genreExists = genreRepository.exists(genre.getName());
        assertThat(genreExists).isTrue();
    }

}