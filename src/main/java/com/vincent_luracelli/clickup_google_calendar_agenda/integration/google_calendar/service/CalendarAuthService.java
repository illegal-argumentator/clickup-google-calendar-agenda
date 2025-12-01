package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import ch.qos.logback.core.util.StringUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.common.dto.TokenPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarAuthService {

    private final GoogleProps googleProps;

    private final CalendarTokenService calendarTokenService;

    public MeResponse me(User user) {
        MeResponse meResponse = MeResponse.builder()
                .success(true)
                .message("Authorized.")
                .build();

        try {
            findCalendarTokenOrThrow(user.getCalendarTokenId());
        } catch (ApiException e) {
            return meResponse.toBuilder().success(false).message(e.getMessage()).build();
        }

        return meResponse;
    }

    public CalendarToken requireAccessTokenByUser(User user) {
        CalendarToken calendarToken = findCalendarTokenOrThrow(user.getCalendarTokenId());

        if (isTokenExpired(calendarToken.getAccessExpiration())) {
            TokenPayload tokenPayload = requireRefreshToken(calendarToken);

            calendarToken = calendarTokenService.update(calendarToken.getUserEmail(), CalendarToken.builder()
                    .accessToken(tokenPayload.getAccessToken())
                    .accessExpiration(tokenPayload.getAccessExpiration())
                    .build());
        }

        return calendarToken;
    }

    public GoogleTokenResponse refresh(String refreshToken) {
        OAuth oauth = googleProps.getOauth();

        try {
            return new GoogleRefreshTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    refreshToken,
                    oauth.getClientId(),
                    oauth.getClientSecret()
            ).execute();
        } catch (GeneralSecurityException | IOException e) {
            log.error(e.getMessage());
            throw new ApiException(e.toString(), HttpStatus.UNAUTHORIZED.value(), SourceType.GOOGLE_CALENDAR);
        }
    }

    private TokenPayload requireRefreshToken(CalendarToken calendarToken) {
        GoogleTokenResponse googleTokenResponse = refresh(calendarToken.getRefreshToken());

        TokenPayload tokenPayload = TokenPayload.builder()
                .accessToken(googleTokenResponse.getAccessToken())
                .accessExpiration(System.currentTimeMillis() + (googleTokenResponse.getExpiresInSeconds() - 60) * 1000)
                .build();

        if (googleTokenResponse.getRefreshToken() != null) {
            tokenPayload.setRefreshToken(googleTokenResponse.getRefreshToken());
        }

        return tokenPayload;
    }

    private boolean isTokenExpired(Long tokenExpiration) {
        return tokenExpiration <= System.currentTimeMillis();
    }

    private CalendarToken findCalendarTokenOrThrow(String id) {
        ApiException apiException = new ApiException(
                "Permission denied. Please finish OAuth flow to proceed.",
                HttpStatus.FORBIDDEN.value()
        );

        if (StringUtil.isNullOrEmpty(id)) {
            throw apiException;
        }

        Optional<CalendarToken> calendarTokenOptional = calendarTokenService.findById(id);
        if (calendarTokenOptional.isEmpty()) {
            throw apiException;
        }

        return calendarTokenOptional.get();
    }
}