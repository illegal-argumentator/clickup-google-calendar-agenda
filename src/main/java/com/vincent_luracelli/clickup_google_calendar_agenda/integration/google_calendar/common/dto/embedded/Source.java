package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Source(
    String title,
    String url) {
}
