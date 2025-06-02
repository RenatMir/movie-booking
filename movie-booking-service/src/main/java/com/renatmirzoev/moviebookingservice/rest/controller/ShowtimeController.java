package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.NotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.ShowtimeMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeRequest;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.CreateShowtimeResponse;
import com.renatmirzoev.moviebookingservice.rest.model.showtime.GetShowtimeResponse;
import com.renatmirzoev.moviebookingservice.service.ShowtimeService;
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
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final ShowtimeMapper showtimeMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetShowtimeResponse> getShowtime(@PathVariable("id") long id) {
        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(id);
        if (showtimeOptional.isEmpty()) {
            throw new NotFoundException("Showtime with id %s not found".formatted(id));
        }

        Showtime showtime = showtimeOptional.get();
        GetShowtimeResponse response = showtimeMapper.toGetShowtimeResponse(showtime);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateShowtimeResponse> createShowtime(@Valid @RequestBody CreateShowtimeRequest request) {
        Showtime showtime = showtimeMapper.toShowtime(request);
        long id = showtimeService.saveShowtime(showtime);

        CreateShowtimeResponse response = new CreateShowtimeResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
