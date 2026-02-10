package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
public class EventUtils {

    public static EventDateTime getStartDate(ClickUpWebhookPayload payload) {
        var historyItemOpt = payload.historyItems().stream()
                .filter(it -> "start_date".equals(it.field()))
                .findFirst();
        if (historyItemOpt.isEmpty()) {
            return null;
        }
        return getEventDateTime(historyItemOpt);
    }

    public static EventDateTime getEndDate(ClickUpWebhookPayload payload) {
        var historyItemOpt = payload.historyItems().stream()
                .filter(it -> "due_date".equals(it.field()))
                .findFirst();
        if (historyItemOpt.isEmpty()) {
            return null;
        }
        return getEventDateTime(historyItemOpt);
    }

    private static EventDateTime getEventDateTime(Optional<ClickUpWebhookPayload.HistoryItem> historyItemOpt) {
        var historyItem = historyItemOpt.orElseThrow();
        if (historyItem.after() == null || historyItem.after().isNull()) {
            return null;
        }
        if (!historyItem.after().isTextual()){
            return null;
        }
        var afterText = historyItem.after().asText();

        if (!StringUtils.hasText(afterText)) {
            return null;
        }
        try {
            var dateTime = Long.parseLong(afterText);
            var instant = java.time.Instant.ofEpochMilli(dateTime);
            var offsetDateTime = java.time.OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
            return new EventDateTime(offsetDateTime, "UTC");
        } catch (Exception e) {
            log.warn("Failed to parse date time from history item: {}", historyItem.after(), e);
            return null;
        }
    }


}
