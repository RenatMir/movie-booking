package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TheaterCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TheaterCacheRepository theaterCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Theater> theaterOptional = theaterCacheRepository.getById(Long.MAX_VALUE);
        assertThat(theaterOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetTheaterById() {
        Theater theater = ModelUtils.theater();
        theaterCacheRepository.save(theater);

        Optional<Theater> theaterOptional = theaterCacheRepository.getById(theater.getId());
        assertThat(theaterOptional).isPresent().contains(theater);
    }

    @Test
    void shouldReturnEmptyWhenTheaterDoesNotExist() {
        Optional<Boolean> exists = theaterCacheRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenTheaterExists() {
        String value = UUID.randomUUID().toString();
        theaterCacheRepository.saveExists(value, Long.MAX_VALUE);

        Optional<Boolean> theaterExists = theaterCacheRepository.exists(value, Long.MAX_VALUE);
        assertThat(theaterExists).isPresent().contains(true);
    }
}