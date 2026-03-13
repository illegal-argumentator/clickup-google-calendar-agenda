package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Tag;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type.TagType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public static String getTitle(ClickUpWebhookPayload payload) {
        var historyItemOpt = payload.historyItems().stream()
                .filter(it -> "after".equals(it.field()))
                .findFirst();
        return historyItemOpt.map(historyItem -> historyItem.after().asText()).orElse(null);

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

    public static boolean anyMatchToItems(Set<String> allowed, List<ClickUpWebhookPayload.HistoryItem> historyItems) {
        return historyItems.stream()
                .filter(it -> StringUtils.hasText(it.field()))
                .anyMatch(item -> allowed.contains(item.field()));
    }

    public static List<Task> filterTasksTagByTagName(List<Task> tasks) {
        return tasks.stream().filter(task -> {
            List<String> tagNames = task.getTags().stream()
                    .map(Tag::name)
                    .filter(io.micrometer.common.util.StringUtils::isNotBlank)
                    .map(tag -> tag.trim().toLowerCase())
                    .toList();


            boolean matches = !tagNames.isEmpty() && tagNames.stream()
                    .allMatch(tagName -> tagName.equals(TagType.BAUSTROM.getTag()) || tagName.equals(TagType.BESTELBON.getTag()));
            log.info("Requested tags: {}. Matches all: {}.", tagNames, matches);
            return matches;
        }).toList();
    }

}
