package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.ShowtimeAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Showtime;
import com.renatmirzoev.moviebookingservice.repository.cache.ShowtimeCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.ShowtimeRepository;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private ShowtimeCacheRepository showtimeCacheRepository;
    @InjectMocks
    private ShowtimeService showtimeService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(showtimeRepository, showtimeCacheRepository);
    }

    @Test
    void shouldNotSaveShowtimeIfShowtimeExistsFromCache() {
        Showtime showtime = ModelUtils.showtime();

        when(showtimeCacheRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> showtimeService.saveShowtime(showtime))
            .isInstanceOf(ShowtimeAlreadyExistsException.class);

        inOrder.verify(showtimeCacheRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeCacheRepository, never()).save(any(Showtime.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveShowtimeIfShowtimeExistsFromDb() {
        Showtime showtime = ModelUtils.showtime();

        when(showtimeCacheRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(Optional.empty());
        when(showtimeRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> showtimeService.saveShowtime(showtime))
            .isInstanceOf(ShowtimeAlreadyExistsException.class);

        inOrder.verify(showtimeCacheRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeCacheRepository).saveExists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveShowtime() {
        Showtime showtime = ModelUtils.showtime();

        when(showtimeCacheRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(Optional.empty());
        when(showtimeRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(false);
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        showtimeService.saveShowtime(showtime);

        inOrder.verify(showtimeCacheRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeCacheRepository).saveExists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeRepository).save(any(Showtime.class));
        inOrder.verify(showtimeCacheRepository).save(any(Showtime.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyShowtimeById() {
        when(showtimeCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(showtimeRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(Long.MAX_VALUE);
        assertThat(showtimeOptional).isEmpty();

        inOrder.verify(showtimeCacheRepository).getById(anyLong());
        inOrder.verify(showtimeRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetShowtimeByIdFromCache() {
        Showtime showtime = ModelUtils.showtime();

        when(showtimeCacheRepository.getById(anyLong())).thenReturn(Optional.of(showtime));

        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(Long.MAX_VALUE);
        assertThat(showtimeOptional).isPresent().contains(showtime);

        inOrder.verify(showtimeCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetShowtimeByIdFromDB() {
        Showtime showtime = ModelUtils.showtime();

        when(showtimeCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(showtimeRepository.getById(anyLong())).thenReturn(Optional.of(showtime));

        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(Long.MAX_VALUE);
        assertThat(showtimeOptional).isPresent().contains(showtime);

        inOrder.verify(showtimeCacheRepository).getById(anyLong());
        inOrder.verify(showtimeRepository).getById(anyLong());
        inOrder.verify(showtimeCacheRepository).save(any(Showtime.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(showtimeCacheRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(Optional.empty());
        when(showtimeRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(existParam);

        boolean exists = showtimeService.showtimeExists(Long.MAX_VALUE, Long.MAX_VALUE, Instant.now());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(showtimeCacheRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verify(showtimeCacheRepository).saveExists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(showtimeCacheRepository.exists(anyLong(), anyLong(), any(Instant.class))).thenReturn(Optional.of(existParam));

        boolean exists = showtimeService.showtimeExists(Long.MAX_VALUE, Long.MAX_VALUE, Instant.now());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(showtimeCacheRepository).exists(anyLong(), anyLong(), any(Instant.class));
        inOrder.verifyNoMoreInteractions();
    }
}