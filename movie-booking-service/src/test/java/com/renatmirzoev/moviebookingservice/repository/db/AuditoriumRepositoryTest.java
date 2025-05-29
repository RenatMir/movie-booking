package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class AuditoriumRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AuditoriumRepository auditoriumRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    private Auditorium auditorium;

    @BeforeEach
    @Transactional
    void init() {
        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        long theaterId = theater.getId();

        auditorium = ModelUtils.auditorium().setTheaterId(theaterId);
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Auditorium> auditoriumOptional = auditoriumRepository.getById(Long.MAX_VALUE);
        assertThat(auditoriumOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetAuditoriumByIdWithEmptyRows() {
        auditorium.setRows(Collections.emptySet());
        long id = auditoriumRepository.save(auditorium);

        Optional<Auditorium> auditoriumOptional = auditoriumRepository.getById(id);
        assertThat(auditoriumOptional).isPresent();

        Auditorium auditoriumGet = auditoriumOptional.get();
        assertThat(auditoriumGet)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .isEqualTo(this.auditorium);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        auditoriumRepository.save(auditorium);

        assertThatException()
            .isThrownBy(() -> auditoriumRepository.save(auditorium))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

}