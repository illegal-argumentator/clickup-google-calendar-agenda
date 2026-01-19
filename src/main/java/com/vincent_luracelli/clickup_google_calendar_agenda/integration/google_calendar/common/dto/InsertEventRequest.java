package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record InsertEventRequest(
        String taskId,
        @NotNull(message = "Start date is required")
        EventDateTime start,

        @NotNull(message = "End date is required")
        EventDateTime end,

        String summary,
        String description,
        String location,
        String colorId,
        List<Attendee> attendees,
        Reminders reminders) {
}
