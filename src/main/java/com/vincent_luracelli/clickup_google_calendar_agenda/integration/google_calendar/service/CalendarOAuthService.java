package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service.CalendarTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config.GoogleProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import io.swagger.v3.oas.models.security.Scopes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarOAuthService {

    private static final String ACCESS_TYPE = "offline";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleProps googleProps;
    private final CalendarTokenService calendarTokenService;
    private final CalendarOAuthTokenService calendarOAuthTokenService;
    private final OkHttpUtil okHttpUtil;

    public AuthorizeResponse authorize() {
        GoogleAuthorizationCodeFlow flow = getFlow();
        String url = flow.newAuthorizationUrl()
                .set("prompt", "consent")
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

            GoogleIdToken googleIdToken = GoogleIdToken.parse(JSON_FACTORY, tokenResponse.getIdToken());
            CalendarToken calendarToken = CalendarToken.builder()
                    .accessToken(tokenResponse.getAccessToken())
                    .accessExpiration(System.currentTimeMillis() + tokenResponse.getExpiresInSeconds())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .calendarId(googleIdToken.getPayload().getEmail())
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
        MeResponse meResponse = MeResponse.builder()
                .message("Authorized.")
                .success(true)
                .build();

        try {
            calendarOAuthTokenService.requireValidToken();
        } catch (ApiException e) {
            meResponse.setMessage("Unauthorized." + e.getMessage());
            meResponse.setSuccess(false);
        }

        return meResponse;
    }

    public void revoke(String email) {
        try {
            Optional<CalendarToken> optionalCalendarToken = calendarTokenService.findByCalendarId(email);

            if (optionalCalendarToken.isEmpty()) {
                throw new ApiException("Already revoked." ,HttpStatus.BAD_REQUEST.value());
            }

            CalendarToken calendarToken = optionalCalendarToken.get();

            Request request = new Request.Builder()
                    .url("https://oauth2.googleapis.com/revoke")
                    .post(RequestBody.create(
                            ("token=" + calendarToken.getAccessToken()),
                            MediaType.get("application/x-www-form-urlencoded")
                    ))
                    .build();

            okHttpUtil.handleApiRequest(SourceType.GOOGLE_CALENDAR, request, Void.class);
            calendarTokenService.deleteByCalendarId(calendarToken.getCalendarId());

            log.info("Google OAuth token revoked successfully");
        } catch (ApiException e) {
            log.error("Error revoking token", e);
            throw new ApiException("Failed to revoke token", HttpStatus.BAD_REQUEST.value(), SourceType.GOOGLE_CALENDAR);
        }
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
