package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Seat extends AbstractEntity implements Comparable<Seat> {
    private long id;
    private long label;
    private long rowId;

    @Override
    public int compareTo(Seat o) {
        return Long.compare(getLabel(), o.getLabel());
    }
}
