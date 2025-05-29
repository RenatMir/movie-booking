package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.notfound.ActorNotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.ActorMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorRequest;
import com.renatmirzoev.moviebookingservice.rest.model.actor.CreateActorResponse;
import com.renatmirzoev.moviebookingservice.rest.model.actor.GetActorResponse;
import com.renatmirzoev.moviebookingservice.service.ActorService;
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
@RequestMapping("/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;
    private final ActorMapper actorMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetActorResponse> getActor(@PathVariable("id") long id) {
        Optional<Actor> actorOptional = actorService.getActorById(id);
        if (actorOptional.isEmpty()) {
            throw new ActorNotFoundException("Actor with id %s not found".formatted(id));
        }

        Actor actor = actorOptional.get();
        GetActorResponse response = actorMapper.toGetActorResponse(actor);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateActorResponse> createActor(@Valid @RequestBody CreateActorRequest request) {
        Actor actor = actorMapper.toActor(request);
        long id = actorService.save(actor);

        CreateActorResponse response = new CreateActorResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
