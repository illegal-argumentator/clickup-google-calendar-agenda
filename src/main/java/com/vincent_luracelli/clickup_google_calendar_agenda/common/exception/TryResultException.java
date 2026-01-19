package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

public class TryResultException extends RuntimeException {
    public TryResultException(Exception exception) {
        super(exception);
    }

    public TryResultException(String message) {
        super(message);
    }

}
