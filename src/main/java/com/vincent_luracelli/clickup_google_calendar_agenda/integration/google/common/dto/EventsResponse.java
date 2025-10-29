package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventsResponse {

    private List<EventResponse> items;

}
