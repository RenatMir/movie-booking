package com.renatmirzoev.moviebookingservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Actor extends AbstractEntity {
    private long id;
    private String fullName;
}
