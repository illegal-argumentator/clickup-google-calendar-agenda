package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public final class TagRemovedModificationStrategy implements EventModificationStrategy {

    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, Task task) {
        if (!EventUtils.hasValidTags(task)) {
            log.info("Delete events for user: {} - found invalid tags.", user.getEmail());
            eventHelper.delete(user, events);
        } else {
            if (events.isEmpty()) {
                log.info("Creating event for user: {}.", user.getEmail());
                eventHelper.create(user, task);
            }
        }
    }

    @Override
    public ModificationType getType() {
        return ModificationType.TAG_REMOVE;
    }
}
