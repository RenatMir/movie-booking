package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class BookingCacheRepository extends AbstractCacheRepository {

    private static final String KEY_BOOKING = "booking:";
    private static final String KEY_BOOKING_EXISTS = "bookingExists:";

    private static final Duration TTL_BOOKING = Duration.ofDays(1);
    private static final Duration TTL_BOOKING_EXISTS = Duration.ofDays(1);

    public BookingCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyBooking(long id) {
        return KEY_BOOKING + id;
    }

    static String keyBookingExists(long showtimeId, long seatId) {
        return KEY_BOOKING_EXISTS + showtimeId + ":" + seatId;
    }

    public void save(Booking booking) {
        save(keyBooking(booking.getId()), JsonUtils.toJson(booking), TTL_BOOKING);
    }

    public Optional<Booking> getById(long id) {
        return get(keyBooking(id))
            .map(value -> JsonUtils.fromJson(value, Booking.class));
    }

    public Optional<Boolean> exists(long showtimeId, long seatId) {
        return get(keyBookingExists(showtimeId, seatId))
            .map(Boolean::parseBoolean);
    }

    public void saveExists(long showtimeId, long seatId) {
        save(keyBookingExists(showtimeId, seatId), Boolean.TRUE.toString(), TTL_BOOKING_EXISTS);
    }

    public void clearCache(Booking booking) {
        delete(keyBooking(booking.getId()));
        delete(keyBookingExists(booking.getShowtimeId(), booking.getSeatId()));
    }
}
