package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded.Creator;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded.Organizer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {

    private String kind;
    private String etag;
    private String id;
    private String status;
    private String htmlLink;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String summary;
    private String description;
    private String location;
    private String colorId;
    private Creator creator;
    private Organizer organizer;
    private EventDateTime start;
    private EventDateTime end;

}
