package com.renatmirzoev.moviebookingservice.rest.model.showtime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class CreateShowtimeRequest {
    @Positive
    private long movieId;
    @Positive
    private long auditoriumId;
    @NotNull
    private Instant dateShow;
}
