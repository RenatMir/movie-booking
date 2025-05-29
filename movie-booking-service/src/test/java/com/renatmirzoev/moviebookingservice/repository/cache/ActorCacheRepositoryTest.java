package com.renatmirzoev.moviebookingservice.repository.cache;

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
class ActorCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ActorCacheRepository actorCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Actor> actorOptional = actorCacheRepository.getById(Long.MAX_VALUE);
        assertThat(actorOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetActorById() {
        Actor actor = ModelUtils.actor();
        actorCacheRepository.save(actor);

        Optional<Actor> actorOptional = actorCacheRepository.getById(actor.getId());
        assertThat(actorOptional).isPresent().contains(actor);
    }

    @Test
    void shouldReturnEmptyWhenActorDoesNotExist() {
        Optional<Boolean> exists = actorCacheRepository.exists(UUID.randomUUID().toString());
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenActorExists() {
        String value = UUID.randomUUID().toString();
        actorCacheRepository.saveExists(value);

        Optional<Boolean> actorExists = actorCacheRepository.exists(value);
        assertThat(actorExists).isPresent().contains(true);
    }
}