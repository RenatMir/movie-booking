package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.ActorAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Actor;
import com.renatmirzoev.moviebookingservice.repository.cache.ActorCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private ActorCacheRepository actorCacheRepository;
    @InjectMocks
    private ActorService actorService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(actorRepository, actorCacheRepository);
    }

    @Test
    void shouldNotSaveActorIfActorExistsFromCache() {
        Actor actor = ModelUtils.actor();

        when(actorCacheRepository.exists(anyString())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> actorService.save(actor))
            .isInstanceOf(ActorAlreadyExistsException.class);

        inOrder.verify(actorCacheRepository).exists(anyString());
        inOrder.verify(actorCacheRepository, never()).save(any(Actor.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveActorIfActorExistsFromDb() {
        Actor actor = ModelUtils.actor();

        when(actorCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(actorRepository.exists(anyString())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> actorService.save(actor))
            .isInstanceOf(ActorAlreadyExistsException.class);

        inOrder.verify(actorCacheRepository).exists(anyString());
        inOrder.verify(actorRepository).exists(anyString());
        inOrder.verify(actorCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveActor() {
        Actor actor = ModelUtils.actor();

        when(actorCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(actorRepository.exists(anyString())).thenReturn(false);
        when(actorRepository.save(any(Actor.class))).thenReturn(actor);

        actorService.save(actor);

        inOrder.verify(actorCacheRepository).exists(anyString());
        inOrder.verify(actorRepository).exists(anyString());
        inOrder.verify(actorCacheRepository).saveExists(anyString());
        inOrder.verify(actorRepository).save(any(Actor.class));
        inOrder.verify(actorCacheRepository).save(any(Actor.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyActorById() {
        when(actorCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(actorRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Actor> actorOptional = actorService.getActorById(Long.MAX_VALUE);
        assertThat(actorOptional).isEmpty();

        inOrder.verify(actorCacheRepository).getById(anyLong());
        inOrder.verify(actorRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetActorByIdFromCache() {
        Actor actor = ModelUtils.actor();

        when(actorCacheRepository.getById(anyLong())).thenReturn(Optional.of(actor));

        Optional<Actor> actorOptional = actorService.getActorById(Long.MAX_VALUE);
        assertThat(actorOptional).isPresent().contains(actor);

        inOrder.verify(actorCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetActorByIdFromDB() {
        Actor actor = ModelUtils.actor();

        when(actorCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(actorRepository.getById(anyLong())).thenReturn(Optional.of(actor));

        Optional<Actor> actorOptional = actorService.getActorById(Long.MAX_VALUE);
        assertThat(actorOptional).isPresent().contains(actor);

        inOrder.verify(actorCacheRepository).getById(anyLong());
        inOrder.verify(actorRepository).getById(anyLong());
        inOrder.verify(actorCacheRepository).save(any(Actor.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(actorCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(actorRepository.exists(anyString())).thenReturn(existParam);

        boolean exists = actorService.actorExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(actorCacheRepository).exists(anyString());
        inOrder.verify(actorRepository).exists(anyString());
        inOrder.verify(actorCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(actorCacheRepository.exists(anyString())).thenReturn(Optional.of(existParam));

        boolean exists = actorService.actorExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(actorCacheRepository).exists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

}