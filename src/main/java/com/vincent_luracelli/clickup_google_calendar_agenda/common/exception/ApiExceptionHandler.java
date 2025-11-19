package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException(ApiException e) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .source(e.getSourceType())
                .body(e.getMessage())
                .code(e.getCode())
                .build();
        return ResponseEntity.status(exceptionResponse.code()).body(exceptionResponse);
    }
}
