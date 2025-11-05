package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<ExceptionPayload> handleApiRequestException(ApiRequestException e) {
        ExceptionPayload exceptionPayload = e.getExceptionPayload();
        return ResponseEntity.status(exceptionPayload.code()).body(exceptionPayload);
    }

}
