package com.renatmirzoev.moviebookingservice.rest.exception;

import com.renatmirzoev.moviebookingservice.rest.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Something went wrong";

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            status = responseStatus.value();
            message = ex.getMessage();
        }

        return buildErrorResponse(status, message);

    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleServerWebInputException(ServerWebInputException ex) {
        log.error(ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request validation failed");
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        log.error(ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is missing");
    }

    @ExceptionHandler(value = {WebExchangeBindException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(BindingResult bindingResult) {
        List<String> errorList =
            bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " -> " + error.getDefaultMessage())
                .toList();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request validation failed", errorList.toString());
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return buildErrorResponse(status, message, null);
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String details) {
        ErrorResponse errorResponse = new ErrorResponse()
            .setStatus(status)
            .setMessage(message)
            .setDetails(details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
