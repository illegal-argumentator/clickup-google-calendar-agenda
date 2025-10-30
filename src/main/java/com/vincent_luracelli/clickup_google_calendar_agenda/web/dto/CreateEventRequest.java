package com.vincent_luracelli.clickup_google_calendar_agenda.web.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded.EventDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateEventRequest {

    @NotNull(message = "Start date is required")
    private EventDateTime start;

    @NotNull(message = "End date is required")
    private EventDateTime end;

    private String title;

    private String description;

    private List<String> assignees;

}
