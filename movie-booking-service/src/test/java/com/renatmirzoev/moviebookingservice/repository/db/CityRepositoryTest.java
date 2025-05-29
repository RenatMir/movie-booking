package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class CityRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountryRepository countryRepository;

    private long countryId;

    @BeforeEach
    @Transactional
    void init() {
        Country country = countryRepository.save(ModelUtils.country());
        countryId = country.getId();
    }

    @Test
    void shouldGetEmptyById() {
        Optional<City> cityOptional = cityRepository.getById(Long.MAX_VALUE);
        assertThat(cityOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetCityById() {
        City city = ModelUtils.city();
        city.setCountryId(countryId);
        city = cityRepository.save(city);

        Optional<City> cityOptional = cityRepository.getById(city.getId());

        assertThat(cityOptional).isPresent().contains(city);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        City city = ModelUtils.city();
        city.setCountryId(countryId);
        cityRepository.save(city);

        assertThatException()
            .isThrownBy(() -> cityRepository.save(city))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenCityDoesNotExist() {
        boolean cityExists = cityRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(cityExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenCityExists() {
        City city = ModelUtils.city();
        city.setCountryId(countryId);
        city = cityRepository.save(city);

        boolean cityExists = cityRepository.exists(city.getName(), city.getCountryId());
        assertThat(cityExists).isTrue();
    }

}