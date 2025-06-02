package com.renatmirzoev.moviebookingservice.rest.model.booking;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetBookingResponse {
    private long id;
    private long userId;
    private long showtimeId;
    private long seatId;
}
