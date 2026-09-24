package com.vincent_luracelli.clickup_google_calendar_agenda.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private String host;

    private int port;

    private String username;

    private String password;

    private List<String> to;

    private String from;

    private String replyTo;

    private Properties properties;

    @Getter
    @Setter
    public static class Properties {

        private String protocol;

        private Boolean auth;

        private StartTls starttls;

        @Getter
        @Setter
        public static class StartTls {

            private Boolean enabled;

        }
    }
}
