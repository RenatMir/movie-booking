package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TheaterRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Theater> theaterOptional = theaterRepository.getById(Long.MAX_VALUE);
        assertThat(theaterOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetTheaterById() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        City city = ModelUtils.city();
        city.setCountryId(country.getId());
        city = cityRepository.save(city);

        Theater theater = ModelUtils.theater();
        theater.setCityId(city.getId());
        theater = theaterRepository.save(theater);

        Optional<Theater> theaterOptional = theaterRepository.getById(theater.getId());

        assertThat(theaterOptional).isPresent().contains(theater);
    }

    @Test
    void shouldReturnFalseWhenTheaterDoesNotExist() {
        boolean theaterExists = theaterRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(theaterExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenTheaterExists() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        City city = ModelUtils.city();
        city.setCountryId(country.getId());
        city = cityRepository.save(city);

        Theater theater = ModelUtils.theater();
        theater.setCityId(city.getId());
        theater = theaterRepository.save(theater);

        boolean theaterExists = theaterRepository.exists(theater.getName(), theater.getCityId());
        assertThat(theaterExists).isTrue();
    }

}