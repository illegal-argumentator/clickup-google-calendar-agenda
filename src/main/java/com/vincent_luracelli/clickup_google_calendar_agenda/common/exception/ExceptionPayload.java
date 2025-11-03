package com.vincent_luracelli.clickup_google_calendar_agenda.common.exception;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Builder;

@Builder
public record ExceptionPayload(
        SourceType source,
        String body,
        int code) {
}
