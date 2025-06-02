package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.AbstractIntegrationTest;
import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BookingCacheRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookingCacheRepository bookingCacheRepository;

    @Test
    void shouldGetEmptyById() {
        Optional<Booking> bookingOptional = bookingCacheRepository.getById(Long.MAX_VALUE);
        assertThat(bookingOptional).isEmpty();
    }

    @Test
    void shouldSaveAndGetBookingById() {
        Booking booking = ModelUtils.booking();
        bookingCacheRepository.save(booking);

        Optional<Booking> bookingOptional = bookingCacheRepository.getById(booking.getId());
        assertThat(bookingOptional).isPresent().contains(booking);
    }

    @Test
    void shouldReturnEmptyWhenBookingDoesNotExist() {
        Optional<Boolean> exists = bookingCacheRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE);
        assertThat(exists).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenBookingExists() {
        bookingCacheRepository.saveExists(Long.MAX_VALUE, Long.MAX_VALUE);

        Optional<Boolean> bookingExists = bookingCacheRepository.exists(Long.MAX_VALUE, Long.MAX_VALUE);
        assertThat(bookingExists).isPresent().contains(true);
    }

    @Test
    void shouldClearCache() {
        Booking booking = ModelUtils.booking();
        bookingCacheRepository.save(booking);
        bookingCacheRepository.saveExists(booking.getShowtimeId(), booking.getSeatId());

        {
            Optional<Booking> bookingOptional = bookingCacheRepository.getById(booking.getId());
            assertThat(bookingOptional).isPresent().contains(booking);
            Optional<Boolean> bookingExists = bookingCacheRepository.exists(booking.getShowtimeId(), booking.getSeatId());
            assertThat(bookingExists).isPresent().contains(true);
        }

        bookingCacheRepository.clearCache(booking);

        {
            Optional<Booking> bookingOptional = bookingCacheRepository.getById(booking.getId());
            assertThat(bookingOptional).isEmpty();
            Optional<Boolean> bookingExists = bookingCacheRepository.exists(booking.getShowtimeId(), booking.getSeatId());
            assertThat(bookingExists).isEmpty();
        }
    }
}