package com.vincent_luracelli.clickup_google_calendar_agenda.security.common.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}