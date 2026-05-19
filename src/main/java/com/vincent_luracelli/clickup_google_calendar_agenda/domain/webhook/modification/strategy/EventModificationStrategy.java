package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import jakarta.annotation.Nullable;

import java.util.List;

public interface EventModificationStrategy {

    void modify(User user, ClickUpWebhookPayload payload, List<Event> events, @Nullable Task task);

    ModificationType getType();

}
