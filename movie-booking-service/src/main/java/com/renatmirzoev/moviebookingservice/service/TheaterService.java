package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.alreadyexists.TheaterAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.repository.cache.TheaterCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterCacheRepository theaterCacheRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public long saveTheater(Theater theater) {
        if (theaterExists(theater.getName(), theater.getCityId())) {
            throw new TheaterAlreadyExistsException("Theater with name %s and cityId %s already exists".formatted(theater.getName(), theater.getCityId()));
        }

        Theater savedTheater = theaterRepository.save(theater);
        theaterCacheRepository.save(savedTheater);
        return savedTheater.getId();
    }

    public Optional<Theater> getTheaterById(long id) {
        return theaterCacheRepository.getById(id).or(() -> {
            Optional<Theater> theaterOptional = theaterRepository.getById(id);
            theaterOptional.ifPresent(theaterCacheRepository::save);
            return theaterOptional;
        });
    }

    public boolean theaterExists(String name, long cityId) {
        return theaterCacheRepository.exists(name, cityId).orElseGet(() -> {
            boolean value = theaterRepository.exists(name, cityId);
            theaterCacheRepository.saveExists(name, cityId);
            return value;
        });
    }

}
