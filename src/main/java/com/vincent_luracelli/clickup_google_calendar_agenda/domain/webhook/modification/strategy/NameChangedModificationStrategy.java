package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public final class NameChangedModificationStrategy implements EventModificationStrategy {

    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, @Nullable Task task) {
        if (events.isEmpty() || task == null) {
            log.warn("Couldn't update custom field for: {}, because no events.", user.getEmail());
            return;
        }

        PatchEventRequest request = PatchEventRequest.builder().summary("\uD83D\uDCC5 [" + task.getList().getName() + "] " + task.getName()).build();
        eventHelper.update(user, events, request);
    }

    @Override
    public ModificationType getType() {
        return ModificationType.NAME_CHANGE;
    }
}
