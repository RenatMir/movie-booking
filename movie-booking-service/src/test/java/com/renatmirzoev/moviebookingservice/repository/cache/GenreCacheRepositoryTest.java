package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class GenreCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GenreCacheRepository genreCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Genre> genreOptional = genreCacheRepository.getById(Long.MAX_VALUE);
        assertThat(genreOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetGenreById() {
        Genre genre = ModelUtils.genre();
        genreCacheRepository.save(genre);

        Optional<Genre> genreOptional = genreCacheRepository.getById(genre.getId());
        assertThat(genreOptional).isPresent().contains(genre);
    }

    @Test
    void shouldReturnEmptyWhenGenreDoesNotExist() {
        Optional<Boolean> exists = genreCacheRepository.exists(UUID.randomUUID().toString());
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenGenreExists() {
        String value = UUID.randomUUID().toString();
        genreCacheRepository.saveExists(value);

        Optional<Boolean> genreExists = genreCacheRepository.exists(value);
        assertThat(genreExists).isPresent().contains(true);
    }
}