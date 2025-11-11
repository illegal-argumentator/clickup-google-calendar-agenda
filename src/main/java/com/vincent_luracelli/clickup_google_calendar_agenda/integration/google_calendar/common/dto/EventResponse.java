package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {

    private String id;
    private String status;
    private String htmlLink;
    private String summary;
    private String description;
    private String location;
    private String colorId;
    private List<Attendee> attendees;
    private EventDateTime start;
    private EventDateTime end;
    private LocalDateTime created;
    private LocalDateTime updated;

}
