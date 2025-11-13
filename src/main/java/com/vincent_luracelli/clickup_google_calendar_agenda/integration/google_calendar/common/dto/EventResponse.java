package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.client.util.DateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
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
    private DateTime created;
    private DateTime updated;

}
