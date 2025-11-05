package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventListResponse {
        private String summery;
        private String description;
        private String timeZone;
        private List<EventResponse> items;
}