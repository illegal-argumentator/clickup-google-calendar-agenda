package com.vincent_luracelli.clickup_google_calendar_agenda.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import lombok.Builder;

@Builder
public record ResponsePayload(
        SourceType source,
        String body,
        int code) {
}
