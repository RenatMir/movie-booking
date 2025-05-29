package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CityCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CityCacheRepository cityCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<City> cityOptional = cityCacheRepository.getById(Long.MAX_VALUE);
        assertThat(cityOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetCityById() {
        City city = ModelUtils.city();
        cityCacheRepository.save(city);

        Optional<City> cityOptional = cityCacheRepository.getById(city.getId());
        assertThat(cityOptional).isPresent().contains(city);
    }

    @Test
    void shouldReturnEmptyWhenCityDoesNotExist() {
        Optional<Boolean> exists = cityCacheRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenCityExists() {
        String value = UUID.randomUUID().toString();
        cityCacheRepository.saveExists(value, Long.MAX_VALUE);

        Optional<Boolean> cityExists = cityCacheRepository.exists(value, Long.MAX_VALUE);
        assertThat(cityExists).isPresent().contains(true);
    }
}