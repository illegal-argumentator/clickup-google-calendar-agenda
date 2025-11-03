package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkingLocationProperties(
    String type,
    CustomLocation customLocation,
    Boolean homeOffice,
    OfficeLocation officeLocation) {
}
