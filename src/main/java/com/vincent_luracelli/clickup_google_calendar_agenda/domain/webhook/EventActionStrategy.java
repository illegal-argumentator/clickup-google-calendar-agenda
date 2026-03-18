package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;

public interface EventActionStrategy {

    void execute(User user, ClickUpWebhookPayload payload);

    WebhookEvent getEvent();

}
