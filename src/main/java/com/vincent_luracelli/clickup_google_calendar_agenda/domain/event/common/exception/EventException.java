package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;

public class EventException extends ApiRequestException {
    public EventException(ExceptionPayload exceptionPayload) {
        super(exceptionPayload);
    }
}
