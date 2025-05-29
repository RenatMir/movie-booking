package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Row extends AbstractEntity {
    private long id;
    private long label;
    private long auditoriumId;
    private Set<Seat> seats;
}
