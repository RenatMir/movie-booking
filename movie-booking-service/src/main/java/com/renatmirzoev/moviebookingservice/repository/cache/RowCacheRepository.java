package com.renatmirzoev.moviebookingservice.repository.cache;

import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.utils.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RowCacheRepository extends AbstractCacheRepository {

    private static final String KEY_ROW = "row:";

    private static final Duration TTL_ROW = Duration.ofDays(1);

    public RowCacheRepository(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    static String keyRow(long id) {
        return KEY_ROW + id;
    }

    public void save(Row row) {
        save(keyRow(row.getId()), JsonUtils.toJson(row), TTL_ROW);
    }

    public Optional<Row> getById(long id) {
        return get(keyRow(id))
            .map(value -> JsonUtils.fromJson(value, Row.class));
    }

}
