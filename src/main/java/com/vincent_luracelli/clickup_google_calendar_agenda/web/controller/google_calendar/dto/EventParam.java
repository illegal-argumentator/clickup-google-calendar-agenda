package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.type.EventUpdates;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventParam {

    @Min(value = 1, message = "Minimum 1 attendee required")
    private Integer maxAttendees;

    private Boolean supportsAttachments;

    private EventUpdates sendUpdates;

}
