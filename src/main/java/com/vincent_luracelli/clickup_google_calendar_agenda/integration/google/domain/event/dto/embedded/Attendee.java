package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Attendee(@NotNull String email) {
}
