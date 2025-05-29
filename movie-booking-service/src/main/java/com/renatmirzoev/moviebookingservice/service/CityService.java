package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.CityAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.repository.cache.CityCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final CityCacheRepository cityCacheRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public long save(City city) {
        if (cityExists(city.getName(), city.getCountryId())) {
            throw new CityAlreadyExistsException("City with name %s and countryId %s already exists".formatted(city.getName(), city.getCountryId()));
        }

        City savedCity = cityRepository.save(city);
        cityCacheRepository.save(savedCity);
        return savedCity.getId();
    }

    public Optional<City> getCityById(long id) {
        return cityCacheRepository.getById(id).or(() -> {
            Optional<City> cityOptional = cityRepository.getById(id);
            cityOptional.ifPresent(cityCacheRepository::save);
            return cityOptional;
        });
    }

    public boolean cityExists(String name, long countryId) {
        return cityCacheRepository.exists(name, countryId).orElseGet(() -> {
            boolean value = cityRepository.exists(name, countryId);
            cityCacheRepository.saveExists(name, countryId);
            return value;
        });
    }

}
