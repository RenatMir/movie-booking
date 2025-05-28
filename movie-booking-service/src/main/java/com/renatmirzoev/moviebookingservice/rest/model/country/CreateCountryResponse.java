package com.renatmirzoev.moviebookingservice.rest.model.country;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCountryResponse {
    private long id;
}
