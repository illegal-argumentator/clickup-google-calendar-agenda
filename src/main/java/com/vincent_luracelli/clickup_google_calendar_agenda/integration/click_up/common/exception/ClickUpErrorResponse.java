package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClickUpErrorResponse(String err) {
}
