package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import lombok.Getter;

@Getter
public class ApiRequestException extends RuntimeException {

    private final ExceptionPayload exceptionPayload;

    public ApiRequestException(ExceptionPayload exceptionPayload) {
        this.exceptionPayload = exceptionPayload;
    }
}
