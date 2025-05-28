package com.renatmirzoev.moviebookingservice.rest.model.country;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetCountryResponse {
    private long id;
    private String name;
}
