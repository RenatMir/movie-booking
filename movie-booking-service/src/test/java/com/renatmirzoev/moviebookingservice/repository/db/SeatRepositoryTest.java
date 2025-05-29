package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class SeatRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RowRepository rowRepository;
    @Autowired
    private AuditoriumRepository auditoriumRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheaterRepository theaterRepository;

    private Seat seat;

    @BeforeEach
    @Transactional
    void init() {
        Country country = countryRepository.save(ModelUtils.country());
        City city = cityRepository.save(ModelUtils.city().setCountryId(country.getId()));
        Theater theater = theaterRepository.save(ModelUtils.theater().setCityId(city.getId()));
        long theaterId = theater.getId();

        Auditorium auditorium = ModelUtils.auditorium().setTheaterId(theaterId);
        long auditoriumId = auditoriumRepository.save(auditorium);

        Row row = ModelUtils.row().setAuditoriumId(auditoriumId);
        Set<Integer> ids = rowRepository.save(Set.of(row));
        int rowId = ids.iterator().next();

        seat = ModelUtils.seat().setRowId(rowId);
    }

    @Test
    void shouldGetEmptyById() {
        Optional<Seat> seatOptional = seatRepository.getById(Long.MAX_VALUE);
        assertThat(seatOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetSeat() {
        Set<Integer> ids = seatRepository.save(Set.of(seat));
        int seatId = ids.iterator().next();

        Optional<Seat> seatOptional = seatRepository.getById(seatId);
        assertThat(seatOptional).isPresent();

        Seat seatGet = seatOptional.get();
        assertThat(seatGet)
            .usingRecursiveComparison()
            .ignoringFields("id", "dateCreated")
            .isEqualTo(this.seat);
    }

    @Test
    void shouldNotBeAbleToSaveDuplicate() {
        seatRepository.save(Set.of(seat));

        assertThatException()
            .isThrownBy(() -> seatRepository.save(Set.of(seat)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

}