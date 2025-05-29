package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.CityNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.CityMapper;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityRequest;
import com.renatmirzoev.moviebookingservice.rest.model.city.CreateCityResponse;
import com.renatmirzoev.moviebookingservice.rest.model.city.GetCityResponse;
import com.renatmirzoev.moviebookingservice.service.CityService;
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
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetCityResponse> getCity(@PathVariable("id") long id) {
        Optional<City> cityOptional = cityService.getCityById(id);
        if (cityOptional.isEmpty()) {
            throw new CityNotFoundException("City with id %s not found".formatted(id));
        }

        City city = cityOptional.get();
        GetCityResponse response = cityMapper.toGetCityResponse(city);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateCityResponse> createCity(@Valid @RequestBody CreateCityRequest request) {
        City city = cityMapper.toCity(request);
        long id = cityService.saveCity(city);

        CreateCityResponse response = new CreateCityResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
