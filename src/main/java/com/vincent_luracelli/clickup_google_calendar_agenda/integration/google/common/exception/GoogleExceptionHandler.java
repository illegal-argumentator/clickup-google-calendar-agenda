package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.exception.CalendarEventException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GoogleExceptionHandler {

    @ExceptionHandler(CalendarEventException.class)
    ResponseEntity<ExceptionPayload> handleCalendarEventException(CalendarEventException e) {
        ExceptionPayload exceptionPayload = e.getExceptionPayload();
        return ResponseEntity.status(exceptionPayload.code()).body(exceptionPayload);
    }

}
