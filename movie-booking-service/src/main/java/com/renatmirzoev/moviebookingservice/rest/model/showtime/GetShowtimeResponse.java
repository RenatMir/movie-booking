package com.renatmirzoev.moviebookingservice.rest.model.showtime;

import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class GetShowtimeResponse {
    private long id;
    private long movieId;
    private long auditoriumId;
    // private Auditorium auditorium;
    private Instant dateShow;
}
