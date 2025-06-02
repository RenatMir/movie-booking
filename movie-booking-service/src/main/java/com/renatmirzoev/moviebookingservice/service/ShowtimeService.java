package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.AlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.repository.cache.ShowtimeCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeCacheRepository showtimeCacheRepository;

    @Transactional
    public long saveShowtime(Showtime showtime) {
        if (showtimeExists(showtime.getMovieId(), showtime.getAuditoriumId(), showtime.getDateShow())) {
            throw new AlreadyExistsException("Showtime with movieId %s, auditoriumId %s and dateShow %s already exists"
                .formatted(showtime.getMovieId(), showtime.getAuditoriumId(), showtime.getDateShow()));
        }

        Showtime savedShowtime = showtimeRepository.save(showtime);
        showtimeCacheRepository.save(savedShowtime);
        return savedShowtime.getId();
    }

    public Optional<Showtime> getShowtimeById(long id) {
        return showtimeCacheRepository.getById(id).or(() -> {
            Optional<Showtime> showtimeOptional = showtimeRepository.getById(id);
            showtimeOptional.ifPresent(showtimeCacheRepository::save);
            return showtimeOptional;
        });
    }

    public boolean showtimeExists(long movieId, long auditoriumId, Instant dateShow) {
        return showtimeCacheRepository.exists(movieId, auditoriumId, dateShow).orElseGet(() -> {
            boolean value = showtimeRepository.exists(movieId, auditoriumId, dateShow);
            showtimeCacheRepository.saveExists(movieId, auditoriumId, dateShow);
            return value;
        });
    }

}
