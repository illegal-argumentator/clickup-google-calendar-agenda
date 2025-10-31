package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Gadget(
    String display,
    Integer height,
    String iconLink,
    String link,
    Map<String, String> preferences,
    String title,
    String type,
    Integer width) {
}
