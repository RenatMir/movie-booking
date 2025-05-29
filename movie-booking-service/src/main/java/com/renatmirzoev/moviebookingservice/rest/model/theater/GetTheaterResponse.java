package com.renatmirzoev.moviebookingservice.rest.model.theater;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetTheaterResponse {
    private long id;
    private String name;
    private long cityId;
}
