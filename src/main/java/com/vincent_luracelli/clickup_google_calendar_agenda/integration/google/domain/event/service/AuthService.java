package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.config.GoogleProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleProps googleProps;

    public String getToken() {
        GoogleProps.Credentials credentialProps = googleProps.getCredentials();
        try {
            GoogleCredentials credentials = ServiceAccountCredentials
                    .fromPkcs8(
                            credentialProps.getClientId(),
                            credentialProps.getClientEmail(),
                            credentialProps.getPrivateKey(),
                            credentialProps.getPrivateKeyId(),
                            credentialProps.getScopes())
                    .createScoped(credentialProps.getScopes())
                    .createDelegated(credentialProps.getUser());

            AccessToken accessToken = refreshTokenIfExpired(credentials);
            return accessToken.getTokenValue();
        } catch (IOException e) {
            log.error(e.getMessage());
            // TODO handle this exception
            throw new RuntimeException(e);
        }
    }

    private AccessToken refreshTokenIfExpired(GoogleCredentials credentials) throws IOException {
        AccessToken accessToken = credentials.getAccessToken();

        if (accessToken == null || !accessToken.getExpirationTime().before(new Date())) {
            credentials.refresh();
            accessToken = credentials.getAccessToken();
        }

        return accessToken;
    }
}
