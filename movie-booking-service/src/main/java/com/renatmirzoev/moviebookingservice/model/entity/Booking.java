package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Booking extends AbstractEntity {
    private long id;
    private long userId;
    private long showtimeId;
    private long seatId;
}
