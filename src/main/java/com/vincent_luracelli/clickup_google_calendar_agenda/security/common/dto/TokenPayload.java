package com.vincent_luracelli.clickup_google_calendar_agenda.security.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPayload {

    private String accessToken;

    private Long accessExpiration;

    private String refreshToken;

}
