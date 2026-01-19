package com.vincent_luracelli.clickup_google_calendar_agenda.common.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "web.backend")
public class WebBackendProps {
    private String domain;
    private String clickUpWebhookPath;
}
