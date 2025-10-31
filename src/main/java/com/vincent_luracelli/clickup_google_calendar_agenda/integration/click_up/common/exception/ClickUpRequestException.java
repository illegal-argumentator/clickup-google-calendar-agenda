package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiRequestException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ExceptionPayload;

public class ClickUpRequestException extends ApiRequestException {
    public ClickUpRequestException(ExceptionPayload exceptionPayload) {
        super(exceptionPayload);
    }
}
