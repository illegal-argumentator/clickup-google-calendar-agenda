package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ColorsResponse(
    Map<String, Color> calendar,
    Map<String, Color> event) {

    public record Color(
            String background,
            String foreground) {
    }
}