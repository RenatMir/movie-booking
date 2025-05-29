package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.repository.cache.SeatCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatCacheRepository seatCacheRepository;

    @Transactional
    public void saveSeats(Set<Seat> seats) {
        seatRepository.save(seats);
    }

    public Optional<Seat> getSeatById(long id) {
        return seatCacheRepository.getById(id)
            .or(() -> {
                Optional<Seat> seatOptional = seatRepository.getById(id);
                seatOptional.ifPresent(seatCacheRepository::save);
                return seatOptional;
            });
    }
}
