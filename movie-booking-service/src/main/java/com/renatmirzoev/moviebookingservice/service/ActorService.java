package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.AlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.repository.cache.ActorCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ActorService {

    private final ActorRepository actorRepository;
    private final ActorCacheRepository actorCacheRepository;

    @Transactional
    public long saveActor(Actor actor) {
        if (actorExists(actor.getFullName())) {
            throw new AlreadyExistsException("Actor with fullName %s already exists".formatted(actor.getFullName()));
        }

        Actor savedActor = actorRepository.save(actor);
        actorCacheRepository.save(savedActor);
        return savedActor.getId();
    }

    public Optional<Actor> getActorById(long id) {
        return actorCacheRepository.getById(id)
            .or(() -> {
                Optional<Actor> actorOptional = actorRepository.getById(id);
                actorOptional.ifPresent(actorCacheRepository::save);
                return actorOptional;
            });
    }

    public boolean actorExists(String fullName) {
        return actorCacheRepository.exists(fullName)
            .orElseGet(() -> {
                boolean value = actorRepository.exists(fullName);
                actorCacheRepository.saveExists(fullName);
                return value;
            });
    }

}
