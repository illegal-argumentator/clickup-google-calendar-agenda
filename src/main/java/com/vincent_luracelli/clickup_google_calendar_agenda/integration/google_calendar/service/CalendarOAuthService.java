package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service.UserService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarOAuthService {

    @Value("${web.client.redirect-url}")
    private String CLIENT_REDIRECT_URL;

    private static final String ACCESS_TYPE = "offline";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleProps googleProps;

    private final CalendarTokenService calendarTokenService;

    private final UserService userService;

    public AuthorizeResponse authorize() {
        GoogleAuthorizationCodeFlow flow = getFlow();

        String url = flow.newAuthorizationUrl()
                .set("prompt", "consent")
                .setState("google_calendar")
                .setRedirectUri(CLIENT_REDIRECT_URL)
                .setAccessType(ACCESS_TYPE)
                .build();

        return AuthorizeResponse.builder()
                .url(url)
                .build();
    }

    public void callback(String code, User user) {
        try {
            GoogleAuthorizationCodeFlow flow = getFlow();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(CLIENT_REDIRECT_URL)
                    .execute();

            GoogleIdToken googleIdToken = GoogleIdToken.parse(JSON_FACTORY, tokenResponse.getIdToken());

            CalendarToken calendarToken = CalendarToken.builder()
                    .accessToken(tokenResponse.getAccessToken())
                    .accessExpiration(System.currentTimeMillis() + tokenResponse.getExpiresInSeconds())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .userEmail(googleIdToken.getPayload().getEmail())
                    .build();

            CalendarToken savedCalendarToken = calendarTokenService.saveOrUpdateIfExists(calendarToken);
            userService.update(user.getEmail(), User.builder().calendarTokenId(savedCalendarToken.getId()).build());
        } catch (IOException e) {
            log.error("Error during OAuth callback", e);
            throw new ApiException(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    SourceType.GOOGLE_CALENDAR
            );
        }
    }

    private GoogleAuthorizationCodeFlow getFlow() {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                    .setWeb(new GoogleClientSecrets.Details()
                            .setClientId(googleProps.getOauth().getClientId())
                            .setClientSecret(googleProps.getOauth().getClientSecret())
                            .setRedirectUris(List.of(CLIENT_REDIRECT_URL))
                    );

            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    clientSecrets,
                    List.of(CalendarScopes.CALENDAR, "openid", "email", "profile")
            ).setAccessType(ACCESS_TYPE).build();

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
