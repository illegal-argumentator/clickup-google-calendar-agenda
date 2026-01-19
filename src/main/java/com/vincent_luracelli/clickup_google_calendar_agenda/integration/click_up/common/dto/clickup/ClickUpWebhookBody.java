package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup;

import java.util.Set;

public record ClickUpWebhookBody(
        String endpoint,
        Set<String> events
) {
}
