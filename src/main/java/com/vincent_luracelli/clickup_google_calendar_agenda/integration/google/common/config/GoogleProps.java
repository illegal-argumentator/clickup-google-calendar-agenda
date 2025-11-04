package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("google")
public class GoogleProps {

    private String calendarId;

    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {
        private String user;
        private String clientId;
        private String clientEmail;
        private String privateKey;
        private String privateKeyId;
        private List<String> scopes;
    }
}
