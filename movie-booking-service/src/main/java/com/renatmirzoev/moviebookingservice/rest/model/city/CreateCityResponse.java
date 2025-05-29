package com.renatmirzoev.moviebookingservice.rest.model.city;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCityResponse {
    private long id;
}
