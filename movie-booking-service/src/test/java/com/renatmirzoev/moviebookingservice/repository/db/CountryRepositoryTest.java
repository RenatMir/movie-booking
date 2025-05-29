package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CountryRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Country> countryOptional = countryRepository.getById(Long.MAX_VALUE);
        assertThat(countryOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetCountryById() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        Optional<Country> countryOptional = countryRepository.getById(country.getId());

        assertThat(countryOptional).isPresent().contains(country);
    }

    @Test
    void shouldReturnFalseWhenCountryDoesNotExist() {
        boolean countryExists = countryRepository.exists(UUID.randomUUID().toString());
        assertThat(countryExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCountryExists() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        boolean countryExists = countryRepository.exists(country.getName());
        assertThat(countryExists).isTrue();
    }

}