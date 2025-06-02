package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditoriumCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AuditoriumCacheRepository auditoriumCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Auditorium> auditoriumOptional = auditoriumCacheRepository.getById(Long.MAX_VALUE);
        assertThat(auditoriumOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetAuditoriumById() {
        Auditorium auditorium = ModelUtils.auditorium();
        auditoriumCacheRepository.save(auditorium);

        Optional<Auditorium> auditoriumOptional = auditoriumCacheRepository.getById(auditorium.getId());
        assertThat(auditoriumOptional).isPresent().contains(auditorium);
    }

    @Test
    void shouldReturnEmptyWhenAuditoriumDoesNotExist() {
        Optional<Boolean> exists = auditoriumCacheRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenAuditoriumExists() {
        String name = UUID.randomUUID().toString();
        long theaterId = Long.MAX_VALUE;
        auditoriumCacheRepository.saveExists(name, theaterId);

        Optional<Boolean> auditoriumExists = auditoriumCacheRepository.exists(name, theaterId);
        assertThat(auditoriumExists).isPresent().contains(true);
    }
}