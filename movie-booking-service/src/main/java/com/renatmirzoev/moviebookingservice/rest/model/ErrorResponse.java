package com.renatmirzoev.moviebookingservice.rest.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
public class ErrorResponse {
    private HttpStatus status;
    private String message;
    private String details;
}
