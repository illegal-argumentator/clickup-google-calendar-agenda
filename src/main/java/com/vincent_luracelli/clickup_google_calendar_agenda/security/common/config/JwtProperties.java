package com.vincent_luracelli.clickup_google_calendar_agenda.security.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secretKey;

    private int accessExpirationTime;

    private int refreshExpirationTime;
}
