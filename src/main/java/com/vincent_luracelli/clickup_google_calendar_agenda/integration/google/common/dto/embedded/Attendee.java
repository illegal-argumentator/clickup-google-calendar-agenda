package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Attendee(
        Integer additionalGuests,
        String comment,
        String displayName,

        @NotNull
        String email,

        Boolean optional,
        Boolean resource,
        String responseStatus) {
}
