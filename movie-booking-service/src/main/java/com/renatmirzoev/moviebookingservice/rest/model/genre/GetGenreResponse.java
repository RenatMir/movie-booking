package com.renatmirzoev.moviebookingservice.rest.model.genre;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetGenreResponse {
    private long id;
    private String name;
}
