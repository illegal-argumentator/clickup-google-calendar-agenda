package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("google")
public class GoogleProps {

    private OAuth oauth;

    @Getter
    @Setter
    public static class OAuth {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
}
