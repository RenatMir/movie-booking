package com.renatmirzoev.moviebookingservice.rest.model.auditorium;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateAuditoriumResponse {
    private long id;
}
