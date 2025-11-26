package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
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
public class CalendarAuthTokenService {

    private final GoogleProps googleProps;

    private final CalendarTokenService calendarTokenService;

    public MeResponse me(String userEmail) {
        MeResponse meResponse = MeResponse.builder()
                .success(true)
                .message("Authorized.")
                .build();

        try {
            findCalendarTokenOrThrow(userEmail);
        } catch (ApiException e) {
            return meResponse.toBuilder().success(false).message(e.getMessage()).build();
        }

        return meResponse;
    }

    public String requireAccessTokenByUserEmail(String userEmail) {
        CalendarToken calendarToken = findCalendarTokenOrThrow(userEmail);

        if (isTokenExpired(calendarToken.getAccessExpiration())) {
            TokenPayload tokenPayload = requireRefreshToken(calendarToken);
            calendarTokenService.save(calendarToken.toBuilder()
                    .accessToken(tokenPayload.getAccessToken())
                    .accessExpiration(tokenPayload.getAccessExpiration())
                    .refreshToken(tokenPayload.getRefreshToken())
                    .build());
        }

        return calendarToken.getAccessToken();
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
                .accessExpiration(System.currentTimeMillis() + googleTokenResponse.getExpiresInSeconds() * 1000)
                .refreshToken(calendarToken.getRefreshToken())
                .build();

        if (googleTokenResponse.getRefreshToken() != null) {
            tokenPayload.setRefreshToken(googleTokenResponse.getRefreshToken());
        }

        return tokenPayload;
    }

    private boolean isTokenExpired(Long tokenExpiration) {
        return tokenExpiration <= System.currentTimeMillis();
    }

    private CalendarToken findCalendarTokenOrThrow(String userEmail) {
        Optional<CalendarToken> calendarTokenOptional = calendarTokenService.findByUserEmail(userEmail);

        if (calendarTokenOptional.isEmpty()) {
            throw new ApiException(
                    "Permission denied. Please finish OAuth flow to proceed.",
                    HttpStatus.FORBIDDEN.value()
            );
        }

        return calendarTokenOptional.get();
    }
}