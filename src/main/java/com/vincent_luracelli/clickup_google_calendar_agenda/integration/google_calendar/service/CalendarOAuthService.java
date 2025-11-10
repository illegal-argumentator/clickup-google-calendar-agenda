package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarOAuthService {

    private static final String ACCESS_TYPE = "offline";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleProps googleProps;
    private final CalendarTokenService calendarTokenService;
    private final CalendarOAuthTokenService calendarOAuthTokenService;

    public AuthorizeResponse authorize() {
        GoogleAuthorizationCodeFlow flow = getFlow();
        String url = flow.newAuthorizationUrl()
                .setRedirectUri(googleProps.getOauth().getRedirectUri())
                .setAccessType(ACCESS_TYPE)
                .build();

        return AuthorizeResponse.builder()
                .url(url)
                .build();
    }

    public void callback(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = getFlow();
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(googleProps.getOauth().getRedirectUri())
                    .execute();

            CalendarToken calendarToken = CalendarToken.builder()
                    .accessToken(tokenResponse.getAccessToken())
                    .accessExpiration(System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000)
                    .refreshToken(tokenResponse.getRefreshToken())
                    .calendarId(googleProps.getCalendarId())
                    .build();

            calendarTokenService.save(calendarToken);

        } catch (IOException e) {
            log.error("Error during OAuth callback", e);
            throw new ApiException(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    SourceType.GOOGLE_CALENDAR
            );
        }
    }

    public MeResponse me() {
        calendarOAuthTokenService.getValidAccessToken();
        return MeResponse.builder()
                .message("Authorized.")
                .success(true)
                .build();
    }

    private GoogleAuthorizationCodeFlow getFlow() {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                    .setWeb(new GoogleClientSecrets.Details()
                            .setClientId(googleProps.getOauth().getClientId())
                            .setClientSecret(googleProps.getOauth().getClientSecret())
                            .setRedirectUris(List.of(googleProps.getOauth().getRedirectUri()))
                    );

            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    clientSecrets,
                    List.of(CalendarScopes.CALENDAR)
            )
                    .setAccessType(ACCESS_TYPE)
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            log.error("Error creating GoogleAuthorizationCodeFlow", e);
            throw new ApiException(
                    e.toString(),
                    HttpStatus.UNAUTHORIZED.value(),
                    SourceType.GOOGLE_CALENDAR
            );
        }
    }
}
