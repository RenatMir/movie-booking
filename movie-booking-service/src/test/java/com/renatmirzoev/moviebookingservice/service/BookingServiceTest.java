package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.AlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.repository.cache.BookingCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.BookingRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingCacheRepository bookingCacheRepository;
    @InjectMocks
    private BookingService bookingService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(bookingRepository, bookingCacheRepository);
    }

    @Test
    void shouldNotSaveBookingIfBookingExistsFromCache() {
        Booking booking = ModelUtils.booking();

        when(bookingCacheRepository.exists(anyLong(), anyLong())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> bookingService.saveBooking(booking))
            .isInstanceOf(AlreadyExistsException.class);

        inOrder.verify(bookingCacheRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingCacheRepository, never()).save(any(Booking.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveBookingIfBookingExistsFromDb() {
        Booking booking = ModelUtils.booking();

        when(bookingCacheRepository.exists(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.exists(anyLong(), anyLong())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> bookingService.saveBooking(booking))
            .isInstanceOf(AlreadyExistsException.class);

        inOrder.verify(bookingCacheRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingCacheRepository).saveExists(anyLong(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveBooking() {
        Booking booking = ModelUtils.booking();

        when(bookingCacheRepository.exists(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.exists(anyLong(), anyLong())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.saveBooking(booking);

        inOrder.verify(bookingCacheRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingCacheRepository).saveExists(anyLong(), anyLong());
        inOrder.verify(bookingRepository).save(any(Booking.class));
        inOrder.verify(bookingCacheRepository).save(any(Booking.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyBookingById() {
        when(bookingCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Booking> bookingOptional = bookingService.getBookingById(Long.MAX_VALUE);
        assertThat(bookingOptional).isEmpty();

        inOrder.verify(bookingCacheRepository).getById(anyLong());
        inOrder.verify(bookingRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetBookingByIdFromCache() {
        Booking booking = ModelUtils.booking();

        when(bookingCacheRepository.getById(anyLong())).thenReturn(Optional.of(booking));

        Optional<Booking> bookingOptional = bookingService.getBookingById(Long.MAX_VALUE);
        assertThat(bookingOptional).isPresent().contains(booking);

        inOrder.verify(bookingCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetBookingByIdFromDB() {
        Booking booking = ModelUtils.booking();

        when(bookingCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.getById(anyLong())).thenReturn(Optional.of(booking));

        Optional<Booking> bookingOptional = bookingService.getBookingById(Long.MAX_VALUE);
        assertThat(bookingOptional).isPresent().contains(booking);

        inOrder.verify(bookingCacheRepository).getById(anyLong());
        inOrder.verify(bookingRepository).getById(anyLong());
        inOrder.verify(bookingCacheRepository).save(any(Booking.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(bookingCacheRepository.exists(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.exists(anyLong(), anyLong())).thenReturn(existParam);

        boolean exists = bookingService.bookingExists(Long.MAX_VALUE, Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(bookingCacheRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingRepository).exists(anyLong(), anyLong());
        inOrder.verify(bookingCacheRepository).saveExists(anyLong(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(bookingCacheRepository.exists(anyLong(), anyLong())).thenReturn(Optional.of(existParam));

        boolean exists = bookingService.bookingExists(Long.MAX_VALUE, Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(bookingCacheRepository).exists(anyLong(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldDeleteBookingAndClearCache() {
        Booking booking = ModelUtils.booking();

        when(bookingRepository.deleteById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(Long.MAX_VALUE);

        inOrder.verify(bookingRepository).deleteById(anyLong());
        inOrder.verify(bookingCacheRepository).clearCache(any(Booking.class));
    }

    @Test
    void shouldNotDeleteBookingAndClearCache() {
        when(bookingRepository.deleteById(anyLong())).thenReturn(Optional.empty());

        bookingService.deleteBooking(Long.MAX_VALUE);

        inOrder.verify(bookingRepository).deleteById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }
}