package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.ExceptionPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionPayload> handleApiException(ApiException e) {
        ExceptionPayload exceptionPayload = ExceptionPayload.builder()
                .source(e.getSourceType())
                .body(e.getMessage())
                .code(e.getCode())
                .build();
        return ResponseEntity.status(exceptionPayload.code()).body(exceptionPayload);
    }
}
