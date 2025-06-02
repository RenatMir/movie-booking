package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.AlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.repository.cache.BookingCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingCacheRepository bookingCacheRepository;

    @Transactional
    public long saveBooking(Booking booking) {
        if (bookingExists(booking.getShowtimeId(), booking.getSeatId())) {
            throw new AlreadyExistsException("Booking with showtimeId %s and seatId %s already exists".formatted(booking.getShowtimeId(), booking.getSeatId())) {
            };
        }

        Booking savedBooking = bookingRepository.save(booking);
        bookingCacheRepository.save(savedBooking);
        return savedBooking.getId();
    }

    public Optional<Booking> getBookingById(long id) {
        return bookingCacheRepository.getById(id).or(() -> {
            Optional<Booking> bookingOptional = bookingRepository.getById(id);
            bookingOptional.ifPresent(bookingCacheRepository::save);
            return bookingOptional;
        });
    }

    public boolean bookingExists(long showtimeId, long seatId) {
        return bookingCacheRepository.exists(showtimeId, seatId).orElseGet(() -> {
            boolean value = bookingRepository.exists(showtimeId, seatId);
            bookingCacheRepository.saveExists(showtimeId, seatId);
            return value;
        });
    }

    @Transactional
    public boolean deleteBooking(long id) {
        Optional<Booking> deleted = bookingRepository.deleteById(id);
        deleted.ifPresent(bookingCacheRepository::clearCache);
        return deleted.isPresent();
    }

}
