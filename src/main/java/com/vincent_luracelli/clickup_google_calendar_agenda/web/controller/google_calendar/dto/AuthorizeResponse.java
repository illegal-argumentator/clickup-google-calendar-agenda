package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorizeResponse(String url) {
}
