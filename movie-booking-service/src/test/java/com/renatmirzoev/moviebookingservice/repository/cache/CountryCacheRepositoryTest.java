package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CountryCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CountryCacheRepository countryCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Country> countryOptional = countryCacheRepository.getById(Long.MAX_VALUE);
        assertThat(countryOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetCountryById() {
        Country country = ModelUtils.country();
        countryCacheRepository.save(country);

        Optional<Country> countryOptional = countryCacheRepository.getById(country.getId());
        assertThat(countryOptional).isPresent().contains(country);
    }

    @Test
    void shouldReturnEmptyWhenCountryDoesNotExist() {
        Optional<Boolean> exists = countryCacheRepository.exists(UUID.randomUUID().toString());
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenCountryExists() {
        String value = UUID.randomUUID().toString();
        countryCacheRepository.saveExists(value);

        Optional<Boolean> countryExists = countryCacheRepository.exists(value);
        assertThat(countryExists).isPresent().contains(true);
    }
}