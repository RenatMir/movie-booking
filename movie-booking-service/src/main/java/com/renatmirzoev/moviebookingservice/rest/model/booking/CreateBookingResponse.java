package com.renatmirzoev.moviebookingservice.rest.model.booking;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateBookingResponse {
    private long id;
}
