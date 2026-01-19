package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

public class ReTryResultException extends RuntimeException {
    public ReTryResultException(Exception exception) {
        super(exception);
    }

    public ReTryResultException(String message) {
        super(message);
    }
}
