package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShowtimeCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ShowtimeCacheRepository showtimeCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Showtime> showtimeOptional = showtimeCacheRepository.getById(Long.MAX_VALUE);
        assertThat(showtimeOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetShowtimeById() {
        Showtime showtime = ModelUtils.showtime();
        showtimeCacheRepository.save(showtime);

        Optional<Showtime> showtimeOptional = showtimeCacheRepository.getById(showtime.getId());
        assertThat(showtimeOptional).isPresent().contains(showtime);
    }

    @Test
    void shouldReturnEmptyWhenShowtimeDoesNotExist() {
        Optional<Boolean> exists = showtimeCacheRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE, Instant.now());
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenShowtimeExists() {
        Instant dateShow = Instant.now();
        showtimeCacheRepository.saveExists(Long.MAX_VALUE, Long.MAX_VALUE, dateShow);

        Optional<Boolean> showtimeExists = showtimeCacheRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE, dateShow);
        assertThat(showtimeExists).isPresent().contains(true);
    }
}