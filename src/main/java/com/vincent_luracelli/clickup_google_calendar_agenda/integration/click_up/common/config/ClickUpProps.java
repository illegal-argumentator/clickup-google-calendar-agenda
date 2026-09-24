package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.config;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.OAuth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "click-up")
public class ClickUpProps {

    private OAuth oauth;

}
