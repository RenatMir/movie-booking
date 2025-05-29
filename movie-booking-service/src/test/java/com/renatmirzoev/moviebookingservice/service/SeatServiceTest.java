package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.repository.cache.SeatCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;
    @Mock
    private SeatCacheRepository seatCacheRepository;
    @InjectMocks
    private SeatService seatService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(seatRepository, seatCacheRepository);
    }

    @Test
    void shouldSaveSeat() {
        Seat seat = ModelUtils.seat();

        when(seatRepository.save(anySet())).thenReturn(Set.of(Integer.MAX_VALUE));

        seatService.saveSeats(Set.of(seat));

        inOrder.verify(seatRepository).save(anySet());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptySeatById() {
        when(seatCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(seatRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Seat> seatOptional = seatService.getSeatById(Long.MAX_VALUE);
        assertThat(seatOptional).isEmpty();

        inOrder.verify(seatCacheRepository).getById(anyLong());
        inOrder.verify(seatRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetSeatByIdFromCache() {
        Seat seat = ModelUtils.seat();

        when(seatCacheRepository.getById(anyLong())).thenReturn(Optional.of(seat));

        Optional<Seat> seatOptional = seatService.getSeatById(Long.MAX_VALUE);
        assertThat(seatOptional).isPresent().contains(seat);

        inOrder.verify(seatCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetSeatByIdFromDB() {
        Seat seat = ModelUtils.seat();

        when(seatCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(seatRepository.getById(anyLong())).thenReturn(Optional.of(seat));

        Optional<Seat> seatOptional = seatService.getSeatById(Long.MAX_VALUE);
        assertThat(seatOptional).isPresent().contains(seat);

        inOrder.verify(seatCacheRepository).getById(anyLong());
        inOrder.verify(seatRepository).getById(anyLong());
        inOrder.verify(seatCacheRepository).save(any(Seat.class));
        inOrder.verifyNoMoreInteractions();
    }
}