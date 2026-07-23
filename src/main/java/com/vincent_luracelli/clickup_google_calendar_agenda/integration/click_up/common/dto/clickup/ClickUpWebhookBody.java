package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClickUpWebhookBody(
        String endpoint,
        Set<String> events
) {
}
