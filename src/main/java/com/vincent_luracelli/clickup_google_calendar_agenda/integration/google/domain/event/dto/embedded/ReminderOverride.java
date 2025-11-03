package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReminderOverride(
        String method,
        Integer minutes) {
}
