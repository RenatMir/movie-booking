package com.renatmirzoev.moviebookingservice.rest.model.movie;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateMovieResponse {
    private long id;
}
