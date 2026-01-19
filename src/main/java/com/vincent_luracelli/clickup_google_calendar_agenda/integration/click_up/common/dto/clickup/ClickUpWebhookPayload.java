package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Top level webhook payload
public record ClickUpWebhookPayload(
        String event,
        @JsonProperty("history_items")
        List<HistoryItem> historyItems,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("team_id") String teamId,
        @JsonProperty("webhook_id") String webhookId
) {
    public record HistoryItem(
            String id,
            int type,
            String date,
            String field,
            @JsonProperty("parent_id") String parentId,
            HistoryData data,
            User user,
            String before,
            String after
    ) {}

    public record User(
            long id,
            String username,
            String email,
            String color,
            String initials,
            @JsonProperty("profilePicture") String profilePicture,
            int role,
            @JsonProperty("role_subtype") int roleSubtype
    ) {}

    public record HistoryData(
            @JsonProperty("due_date_time") boolean dueDateTime,
            @JsonProperty("old_due_date_time") boolean oldDueDateTime
    ) {}

}
