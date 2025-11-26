package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private SourceType sourceType = SourceType.API;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, SourceType sourceType) {
        super(message);
        this.sourceType = sourceType;
    }
}
