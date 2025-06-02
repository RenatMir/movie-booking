package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.NotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.TheaterMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterRequest;
import com.renatmirzoev.moviebookingservice.rest.model.theater.CreateTheaterResponse;
import com.renatmirzoev.moviebookingservice.rest.model.theater.GetTheaterResponse;
import com.renatmirzoev.moviebookingservice.service.TheaterService;
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
@RequestMapping("/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;
    private final TheaterMapper theaterMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetTheaterResponse> getTheater(@PathVariable("id") long id) {
        Optional<Theater> theaterOptional = theaterService.getTheaterById(id);
        if (theaterOptional.isEmpty()) {
            throw new NotFoundException("Theater with id %s not found".formatted(id));
        }

        Theater theater = theaterOptional.get();
        GetTheaterResponse response = theaterMapper.toGetTheaterResponse(theater);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateTheaterResponse> createTheater(@Valid @RequestBody CreateTheaterRequest request) {
        Theater theater = theaterMapper.toTheater(request);
        long id = theaterService.saveTheater(theater);

        CreateTheaterResponse response = new CreateTheaterResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
