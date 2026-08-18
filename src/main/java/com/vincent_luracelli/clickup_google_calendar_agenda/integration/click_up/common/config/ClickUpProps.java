package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.config;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "click-up")
public class ClickUpProps {

    private OAuth oauth;

}
