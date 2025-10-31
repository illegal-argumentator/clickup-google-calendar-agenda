package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OriginalStartTime(
        LocalDate date,
        OffsetDateTime dateTime,
        String timeZone) {
}
