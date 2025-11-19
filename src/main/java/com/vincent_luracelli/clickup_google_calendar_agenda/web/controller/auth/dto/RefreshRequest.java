package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshRequest(
    @NotBlank(message = "Refresh token is required.")
    String refreshToken) {
}
