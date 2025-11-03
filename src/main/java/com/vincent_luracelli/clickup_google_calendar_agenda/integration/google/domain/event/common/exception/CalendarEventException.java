package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;

public class CalendarEventException extends ApiRequestException {
    public CalendarEventException(ExceptionPayload exceptionPayload) {
        super(exceptionPayload);
    }
}
