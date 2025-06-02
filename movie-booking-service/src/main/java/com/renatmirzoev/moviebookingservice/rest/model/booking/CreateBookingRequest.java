package com.renatmirzoev.moviebookingservice.rest.model.booking;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateBookingRequest {
    @Positive
    private long userId;
    @Positive
    private long showtimeId;
    @Positive
    private long seatId;
}
