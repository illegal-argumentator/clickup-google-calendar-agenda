package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class EventDateUtils {

    private static final String UTC = "UTC";

    public static TaskTimeline retrieveTaskTimeline(Task task) {
        EventDateTime start = initializeNowDate();
        EventDateTime end = initializeNowDate();

        if (task.getStartDate() == null) {
            if (task.getDueDate() != null) {
                start = start.toBuilder().dateTime(parseTimestamp(task.getDueDate())).build();
            }
        } else {
            start = start.toBuilder().dateTime(parseTimestamp(task.getStartDate())).build();
        }

        if (task.getDueDate() == null) {
            if (task.getStartDate() != null) {
                end = end.toBuilder().dateTime(parseTimestamp(task.getStartDate())).build();
            }
        } else {
            end = end.toBuilder().dateTime(parseTimestamp(task.getDueDate())).build();
        }

        return new TaskTimeline(start, end);
    }

    private static OffsetDateTime parseTimestamp(String value) {
        Instant instant;

        try {
            instant = Instant.ofEpochMilli(Long.parseLong(value));
        } catch (NumberFormatException e) {
            instant = Instant.now();
        }

        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static EventDateTime initializeNowDate() {
        return EventDateTime.builder()
                .dateTime(OffsetDateTime.now(ZoneOffset.UTC))
                .timeZone(UTC)
                .build();
    }

    public record TaskTimeline(
            EventDateTime start,
            EventDateTime end) {
    }

}
