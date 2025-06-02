package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.AuditoriumNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.AuditoriumMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.CreateAuditoriumRequest;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.CreateAuditoriumResponse;
import com.renatmirzoev.moviebookingservice.rest.model.auditorium.GetAuditoriumResponse;
import com.renatmirzoev.moviebookingservice.service.AuditoriumService;
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
@RequestMapping("/auditoriums")
@RequiredArgsConstructor
public class AuditoriumController {

    private final AuditoriumService auditoriumService;
    private final AuditoriumMapper auditoriumMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetAuditoriumResponse> getAuditorium(@PathVariable("id") long id) {
        Optional<Auditorium> auditoriumOptional = auditoriumService.getAuditoriumById(id);
        if (auditoriumOptional.isEmpty()) {
            throw new AuditoriumNotFoundException("Auditorium with id %s not found".formatted(id));
        }

        GetAuditoriumResponse response = auditoriumMapper.toGetAuditoriumResponse(auditoriumOptional.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateAuditoriumResponse> createAuditorium(@Valid @RequestBody CreateAuditoriumRequest request) {
        Auditorium auditorium = auditoriumMapper.toAuditorium(request);
        long id = auditoriumService.saveAuditorium(auditorium);

        CreateAuditoriumResponse response = new CreateAuditoriumResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
