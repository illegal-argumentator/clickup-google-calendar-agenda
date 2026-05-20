package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateChangedModificationStrategy implements EventModificationStrategy {

    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, Task task) {
        if (events.isEmpty()) {
            log.warn("Couldn't update date for: {}, because no events.", user.getEmail());
            return;
        }

        PatchEventRequest request = PatchEventRequest.builder()
                .start(new EventDateTime(task.getOffsetStartDate(), "UTC"))
                .end(new EventDateTime(task.getOffsetDueDate(), "UTC"))
                .build();

        eventHelper.update(user, events, request);
    }

    @Override
    public ModificationType getType() {
        return ModificationType.DATE_CHANGE;
    }
}
