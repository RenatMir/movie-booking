package com.renatmirzoev.moviebookingservice.rest.model.showtime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateShowtimeResponse {
    private long id;
}
