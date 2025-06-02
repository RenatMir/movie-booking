package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Showtime extends AbstractEntity {
    private long id;
    private long movieId;
    private long auditoriumId;
    private Instant dateShow;

    public static final ChronoUnit DATE_SHOW_TRUNCATION = ChronoUnit.MINUTES;

    public Showtime setDateShow(Instant dateShow) {
        this.dateShow = dateShow.truncatedTo(DATE_SHOW_TRUNCATION);
        return this;
    }
}
