package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    private SourceType sourceType = SourceType.API;

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String message, SourceType sourceType) {
        super(message);
        this.sourceType = sourceType;
    }
}
