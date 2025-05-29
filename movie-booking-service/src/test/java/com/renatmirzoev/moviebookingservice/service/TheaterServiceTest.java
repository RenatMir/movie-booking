package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.TheaterAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Theater;
import com.renatmirzoev.moviebookingservice.repository.cache.TheaterCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.TheaterRepository;
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
class TheaterServiceTest {

    @Mock
    private TheaterRepository theaterRepository;
    @Mock
    private TheaterCacheRepository theaterCacheRepository;
    @InjectMocks
    private TheaterService theaterService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(theaterRepository, theaterCacheRepository);
    }

    @Test
    void shouldNotSaveTheaterIfTheaterExistsFromCache() {
        Theater theater = ModelUtils.theater();

        when(theaterCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> theaterService.save(theater))
            .isInstanceOf(TheaterAlreadyExistsException.class);

        inOrder.verify(theaterCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterCacheRepository, never()).save(any(Theater.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveTheaterIfTheaterExistsFromDb() {
        Theater theater = ModelUtils.theater();

        when(theaterCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(theaterRepository.exists(anyString(), anyLong())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> theaterService.save(theater))
            .isInstanceOf(TheaterAlreadyExistsException.class);

        inOrder.verify(theaterCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveTheater() {
        Theater theater = ModelUtils.theater();

        when(theaterCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(theaterRepository.exists(anyString(), anyLong())).thenReturn(false);
        when(theaterRepository.save(any(Theater.class))).thenReturn(theater);

        theaterService.save(theater);

        inOrder.verify(theaterCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verify(theaterRepository).save(any(Theater.class));
        inOrder.verify(theaterCacheRepository).save(any(Theater.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyTheaterById() {
        when(theaterCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(theaterRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Theater> theaterOptional = theaterService.getTheaterById(Long.MAX_VALUE);
        assertThat(theaterOptional).isEmpty();

        inOrder.verify(theaterCacheRepository).getById(anyLong());
        inOrder.verify(theaterRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetTheaterByIdFromCache() {
        Theater theater = ModelUtils.theater();

        when(theaterCacheRepository.getById(anyLong())).thenReturn(Optional.of(theater));

        Optional<Theater> theaterOptional = theaterService.getTheaterById(Long.MAX_VALUE);
        assertThat(theaterOptional).isPresent().contains(theater);

        inOrder.verify(theaterCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetTheaterByIdFromDB() {
        Theater theater = ModelUtils.theater();

        when(theaterCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(theaterRepository.getById(anyLong())).thenReturn(Optional.of(theater));

        Optional<Theater> theaterOptional = theaterService.getTheaterById(Long.MAX_VALUE);
        assertThat(theaterOptional).isPresent().contains(theater);

        inOrder.verify(theaterCacheRepository).getById(anyLong());
        inOrder.verify(theaterRepository).getById(anyLong());
        inOrder.verify(theaterCacheRepository).save(any(Theater.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(theaterCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(theaterRepository.exists(anyString(), anyLong())).thenReturn(existParam);

        boolean exists = theaterService.theaterExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(theaterCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterRepository).exists(anyString(), anyLong());
        inOrder.verify(theaterCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(theaterCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(existParam));

        boolean exists = theaterService.theaterExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(theaterCacheRepository).exists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }
}