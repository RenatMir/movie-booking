package com.renatmirzoev.moviebookingservice.rest.model.city;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetCityResponse {
    private long id;
    private String name;
    private long countryId;
}
