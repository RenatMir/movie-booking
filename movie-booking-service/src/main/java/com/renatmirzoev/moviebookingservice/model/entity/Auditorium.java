package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Auditorium extends AbstractEntity {
    private long id;
    private String name;
    private long theaterId;
    private Set<Row> rows;
}
