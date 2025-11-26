package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service.ClickUpTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpOAuthClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.config.ClickUpProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickUpOAuthService {

    @Value("${web.client.redirect-url}")
    private String CLIENT_REDIRECT_URL;

    private static final String CLICK_UP_AUTH_URL_TEMPLATE = "https://app.clickup.com/api?client_id=%s&redirect_uri=%s&state=click_up";

    private final ClickUpProps clickUpProps;

    private final ClickUpOAuthClient clickUpOAuthClient;

    private final ClickUpTokenService clickUpTokenService;

    private final JwtUserDetailsService jwtUserDetailsService;

    public AuthorizeResponse authorize() {
        OAuth oauth = clickUpProps.getOauth();
        return AuthorizeResponse.builder()
                .url(CLICK_UP_AUTH_URL_TEMPLATE.formatted(oauth.getClientId(), CLIENT_REDIRECT_URL))
                .build();
    }

    public void callback(String code) {
        OAuth oauth = clickUpProps.getOauth();
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .clientId(oauth.getClientId())
                .clientSecret(oauth.getClientSecret())
                .code(code)
                .build();

        AccessTokenResponse accessToken = clickUpOAuthClient.getAccessToken(accessTokenRequest);
        UserDetails userDetails = jwtUserDetailsService.getUserFromContext();

        ClickUpToken clickUpToken = ClickUpToken.builder()
                .accessToken("Bearer " + accessToken.accessToken())
                .userEmail(userDetails.getUsername())
                .build();
        clickUpTokenService.save(clickUpToken);
    }
}
