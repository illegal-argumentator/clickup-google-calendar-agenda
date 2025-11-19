package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponse (
        @JsonProperty("access-token")
        String accessToken,

        @JsonProperty("refresh-token")
        String refreshToken) {
}
