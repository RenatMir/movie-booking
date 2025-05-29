package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.CountryNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.CountryMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryRequest;
import com.renatmirzoev.moviebookingservice.rest.model.country.CreateCountryResponse;
import com.renatmirzoev.moviebookingservice.rest.model.country.GetCountryResponse;
import com.renatmirzoev.moviebookingservice.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetCountryResponse> getCountry(@PathVariable("id") long id) {
        Optional<Country> countryOptional = countryService.getCountryById(id);
        if (countryOptional.isEmpty()) {
            throw new CountryNotFoundException("Country with id %s not found".formatted(id));
        }

        Country country = countryOptional.get();
        GetCountryResponse response = countryMapper.toGetCountryResponse(country);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateCountryResponse> createCountry(@Valid @RequestBody CreateCountryRequest request) {
        Country country = countryMapper.toCountry(request);
        long id = countryService.save(country);

        CreateCountryResponse response = new CreateCountryResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
