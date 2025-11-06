package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarOAuthTokenService {

    private final GoogleProps googleProps;

    private final CalendarTokenService calendarTokenService;

    public String getValidAccessToken() {
        String calendarId = googleProps.getCalendarId();
        Optional<CalendarToken> calendarTokenOptional = calendarTokenService.findByCalendarId(calendarId);

        if (calendarTokenOptional.isEmpty()) {
            throw new ApiException(
                    "Permission denied. Please finish OAuth flow to proceed.",
                    HttpStatus.FORBIDDEN.value()
            );
        }

        CalendarToken calendarToken = calendarTokenOptional.get();

        if (calendarToken.getAccessTokenExpiration() <= System.currentTimeMillis()) {
            GoogleTokenResponse googleTokenResponse = refresh(calendarToken.getRefreshToken());

            calendarToken.setAccessToken(googleTokenResponse.getAccessToken());
            calendarToken.setAccessTokenExpiration(
                    System.currentTimeMillis() + googleTokenResponse.getExpiresInSeconds() * 1000
            );

            if (googleTokenResponse.getRefreshToken() != null) {
                calendarToken.setRefreshToken(googleTokenResponse.getRefreshToken());
            }

            calendarTokenService.save(calendarToken);
        }

        return calendarToken.getAccessToken();
    }

    public GoogleTokenResponse refresh(String refreshToken) {
        GoogleProps.OAuth oauth = googleProps.getOauth();

        try {
            return new GoogleRefreshTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    refreshToken,
                    oauth.getClientId(),
                    oauth.getClientSecret()
            ).execute();
        } catch (GeneralSecurityException | IOException e) {
            throw new ApiException(e.toString(), HttpStatus.UNAUTHORIZED.value(), SourceType.GOOGLE_CALENDAR);
        }
    }
}