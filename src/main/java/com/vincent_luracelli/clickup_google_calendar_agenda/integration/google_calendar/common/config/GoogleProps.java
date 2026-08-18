package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.config;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("google")
public class GoogleProps {

    private OAuth oauth = new OAuth();

}
