package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class TheaterRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;

    private long cityId;

    @BeforeEach
    @Transactional
    void init() {
        Country country = ModelUtils.country();
        country = countryRepository.save(country);

        City city = ModelUtils.city();
        city.setCountryId(country.getId());
        city = cityRepository.save(city);
        cityId = city.getId();
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Theater> theaterOptional = theaterRepository.getById(Long.MAX_VALUE);
        assertThat(theaterOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetTheaterById() {
        Theater theater = ModelUtils.theater();
        theater.setCityId(cityId);
        theater = theaterRepository.save(theater);

        Optional<Theater> theaterOptional = theaterRepository.getById(theater.getId());

        assertThat(theaterOptional).isPresent().contains(theater);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        Theater theater = ModelUtils.theater();
        theater.setCityId(cityId);
        theaterRepository.save(theater);

        assertThatException()
            .isThrownBy(() -> theaterRepository.save(theater))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldReturnFalseWhenTheaterDoesNotExist() {
        boolean theaterExists = theaterRepository.exists(UUID.randomUUID().toString(), Long.MAX_VALUE);
        assertThat(theaterExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenTheaterExists() {
        Theater theater = ModelUtils.theater();
        theater.setCityId(cityId);
        theater = theaterRepository.save(theater);

        boolean theaterExists = theaterRepository.exists(theater.getName(), theater.getCityId());
        assertThat(theaterExists).isTrue();
    }

}