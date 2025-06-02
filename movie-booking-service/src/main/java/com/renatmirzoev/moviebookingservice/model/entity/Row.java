package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.SortedSet;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Row extends AbstractEntity implements Comparable<Row> {
    private long id;
    private long label;
    private long auditoriumId;
    private SortedSet<Seat> seats;

    public Seat seatById(long seatId) {
        return seats.stream()
            .filter(seat -> seat.getId() == seatId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public int compareTo(Row o) {
        return Long.compare(getLabel(), o.getLabel());
    }
}
