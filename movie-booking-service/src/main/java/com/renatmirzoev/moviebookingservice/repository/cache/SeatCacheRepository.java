package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Seat;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class SeatCacheRepository extends AbstractCacheRepository {

    private static final String KEY_SEAT = "seat:";

    private static final Duration TTL_SEAT = Duration.ofDays(1);

    public SeatCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keySeat(long id) {
        return KEY_SEAT + id;
    }

    public void save(Seat seat) {
        save(keySeat(seat.getId()), JsonUtils.toJson(seat), TTL_SEAT);
    }

    public Optional<Seat> getById(long id) {
        return get(keySeat(id))
            .map(value -> JsonUtils.fromJson(value, Seat.class));
    }

}
