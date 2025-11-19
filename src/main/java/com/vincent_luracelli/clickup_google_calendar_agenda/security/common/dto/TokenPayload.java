package com.vincent_luracelli.clickup_google_calendar_agenda.security.common.dto;

import lombok.Builder;

@Builder
public record TokenPayload(
        String accessToken,
        String refreshToken) {
}
