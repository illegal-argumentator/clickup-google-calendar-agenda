package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record EventDateTime(
        OffsetDateTime dateTime,
        String timeZone) {
}
