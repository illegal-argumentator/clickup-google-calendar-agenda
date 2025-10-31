package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ClickUpExceptionHandler {

    @ExceptionHandler(ClickUpRequestException.class)
    ResponseEntity<ExceptionPayload> handleClickUpRequestException(ClickUpRequestException e) {
        ExceptionPayload exceptionPayload = e.getExceptionPayload();
        return ResponseEntity.status(exceptionPayload.code()).body(exceptionPayload);
    }
}
