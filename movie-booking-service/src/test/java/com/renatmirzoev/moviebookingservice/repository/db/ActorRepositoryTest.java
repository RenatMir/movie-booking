package com.renatmirzoev.moviebookingservice.repository.db;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ActorRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Actor> actorOptional = actorRepository.getById(Long.MAX_VALUE);
        assertThat(actorOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetActorById() {
        Actor actor = ModelUtils.actor();
        actor = actorRepository.save(actor);

        Optional<Actor> actorOptional = actorRepository.getById(actor.getId());

        assertThat(actorOptional).isPresent().contains(actor);
    }

    @Test
    void shouldReturnFalseWhenActorDoesNotExist() {
        boolean actorExists = actorRepository.exists(UUID.randomUUID().toString());
        assertThat(actorExists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenActorExists() {
        Actor actor = ModelUtils.actor();
        actor = actorRepository.save(actor);

        boolean actorExists = actorRepository.exists(actor.getFullName());
        assertThat(actorExists).isTrue();
    }

}