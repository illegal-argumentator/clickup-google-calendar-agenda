package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventListResponse {
        private List<EventResponse> items;
        private String nextTokenPage;
}