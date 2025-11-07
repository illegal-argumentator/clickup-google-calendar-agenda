package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import lombok.Builder;

@Builder
public record MeResponse(
        Boolean success,
        String message) {
}
