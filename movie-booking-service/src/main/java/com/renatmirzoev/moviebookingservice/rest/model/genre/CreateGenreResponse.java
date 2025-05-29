package com.renatmirzoev.moviebookingservice.rest.model.genre;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateGenreResponse {
    private long id;
}
