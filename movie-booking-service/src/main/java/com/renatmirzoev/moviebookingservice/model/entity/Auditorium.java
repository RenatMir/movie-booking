package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.SortedSet;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Auditorium extends AbstractEntity {
    private long id;
    private String name;
    private long theaterId;
    private SortedSet<Row> rows;

    public Row rowById(long rowId) {
        return rows.stream()
            .filter(row -> row.getId() == rowId)
            .findFirst()
            .orElse(null);
    }
}
