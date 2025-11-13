package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private SourceType sourceType = SourceType.API;

    private final String message;

    private final int code;

    public ApiException(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ApiException(String message, int code, SourceType sourceType) {
        this.message = message;
        this.code = code;
        this.sourceType = sourceType;
    }
}
