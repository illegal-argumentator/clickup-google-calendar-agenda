package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service.ClickUpTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.service.UserService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpOAuthClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.config.ClickUpProps;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.AccessTokenResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.vincent_luracelli.clickup_google_calendar_agenda.security.common.constants.AuthConstants.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
public class ClickUpOAuthService {

    @Value("${web.client.redirect-url}")
    private String CLIENT_REDIRECT_URL;

    private static final String CLICK_UP_AUTH_URL_TEMPLATE = "https://app.clickup.com/api?client_id=%s&redirect_uri=%s&state=click_up";

    private final ClickUpProps clickUpProps;

    private final ClickUpOAuthClient clickUpOAuthClient;

    private final ClickUpTokenService clickUpTokenService;

    private final UserService userService;

    public AuthorizeResponse authorize() {
        OAuth oauth = clickUpProps.getOauth();
        return AuthorizeResponse.builder()
                .url(CLICK_UP_AUTH_URL_TEMPLATE.formatted(oauth.getClientId(), CLIENT_REDIRECT_URL))
                .build();
    }

    public void callback(String code, User user) {
        OAuth oauth = clickUpProps.getOauth();
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .clientId(oauth.getClientId())
                .clientSecret(oauth.getClientSecret())
                .code(code)
                .build();

        AccessTokenResponse accessToken = clickUpOAuthClient.getAccessToken(accessTokenRequest);

        ClickUpToken clickUpToken = ClickUpToken.builder()
                .accessToken(BEARER_PREFIX + accessToken.accessToken())
                .userEmail(user.getUsername())
                .build();

        ClickUpToken savedClickUpToken = clickUpTokenService.saveOrUpdateIfExists(clickUpToken);
        userService.update(user.getEmail(), User.builder().clickUpTokenId(savedClickUpToken.getId()).build());
    }
}
