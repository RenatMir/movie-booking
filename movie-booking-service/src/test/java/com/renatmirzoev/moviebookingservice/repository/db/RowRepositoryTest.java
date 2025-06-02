package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class RowRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private RowRepository rowRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private AuditoriumRepository auditoriumRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    private Row row;

    @BeforeEach
    @Transactional
    void init() {
        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        long theaterId = theater.getId();

        Auditorium auditorium = ModelUtils.auditorium().setTheaterId(theaterId);
        long auditoriumId = auditoriumRepository.save(auditorium);

        row = ModelUtils.row().setAuditoriumId(auditoriumId);
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Row> rowOptional = rowRepository.getById(Long.MAX_VALUE);
        assertThat(rowOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetRowWithEmptySeats() {
        row.setSeats(new TreeSet<>());
        long rowId = rowRepository.save(row);

        Optional<Row> rowOptional = rowRepository.getById(rowId);
        assertThat(rowOptional).isPresent();

        Row rowGet = rowOptional.get();
        assertThat(rowGet)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .isEqualTo(this.row);
    }

    @Test
    void shouldSaveAndGetRow() {
        long rowId = rowRepository.save(row);
        row.getSeats().forEach(seat -> seat.setRowId(rowId));
        seatRepository.save(row.getSeats());

        Optional<Row> rowOptional = rowRepository.getById(rowId);
        assertThat(rowOptional).isPresent();

        Row rowGet = rowOptional.get();
        assertThat(rowGet)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .ignoringFields("seats.id", "seats.dateCreated")
            .isEqualTo(this.row);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        rowRepository.save(row);

        assertThatException()
            .isThrownBy(() -> rowRepository.save(row))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

}