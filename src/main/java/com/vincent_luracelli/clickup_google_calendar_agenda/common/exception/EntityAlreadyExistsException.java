package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    private final SourceType sourceType = SourceType.API;

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

}
