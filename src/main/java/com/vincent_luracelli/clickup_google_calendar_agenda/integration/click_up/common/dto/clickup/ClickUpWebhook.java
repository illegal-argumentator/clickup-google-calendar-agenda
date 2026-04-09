package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClickUpWebhook(
        String id,
        @JsonProperty("team_id")
        String teamId,
        String endpoint,
        Set<String> events,
        String secret,
        Health health
) {

    public record Health(
            String status,
            @JsonProperty("fail_count")
            int failCount
    ) {}

}
