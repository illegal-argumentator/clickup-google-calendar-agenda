package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;

import java.util.List;

public interface EventModificationStrategy {

    void modify(User user, ClickUpWebhookPayload payload, List<Event> events);

    ModificationType getType();

}
