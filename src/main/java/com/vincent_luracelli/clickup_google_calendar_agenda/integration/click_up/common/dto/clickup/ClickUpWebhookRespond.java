package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClickUpWebhookRespond(
        List<ClickUpWebhook> webhooks
) {
}
