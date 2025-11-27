package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final SourceType sourceType = SourceType.API;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
