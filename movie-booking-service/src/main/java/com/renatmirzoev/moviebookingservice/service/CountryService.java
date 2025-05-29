package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.alreadyexists.CountryAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.repository.cache.CountryCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryCacheRepository countryCacheRepository;

    @Transactional
    public long saveCountry(Country country) {
        if (countryExists(country.getName())) {
            throw new CountryAlreadyExistsException("Country with name %s already exists".formatted(country.getName()));
        }

        Country savedCountry = countryRepository.save(country);
        countryCacheRepository.save(savedCountry);
        return savedCountry.getId();
    }

    public Optional<Country> getCountryById(long id) {
        return countryCacheRepository.getById(id)
            .or(() -> {
                Optional<Country> countryOptional = countryRepository.getById(id);
                countryOptional.ifPresent(countryCacheRepository::save);
                return countryOptional;
            });
    }

    public boolean countryExists(String name) {
        return countryCacheRepository.exists(name)
            .orElseGet(() -> {
                boolean value = countryRepository.exists(name);
                countryCacheRepository.saveExists(name);
                return value;
            });
    }

}
