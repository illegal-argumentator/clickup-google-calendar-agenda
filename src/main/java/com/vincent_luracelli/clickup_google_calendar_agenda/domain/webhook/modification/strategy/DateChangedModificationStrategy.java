package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryResult;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateChangedModificationStrategy implements EventModificationStrategy {

    private final EventClient eventClient;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, Task task) {
        EventParam params = EventParam.builder()
                .sendUpdates(EventUpdates.NONE)
                .supportsAttachments(false)
                .build();

        for (Event event : events) {
            PatchEventRequest request = PatchEventRequest.builder()
                    .start(new EventDateTime(task.getOffsetStartDate(), "UTC"))
                    .end(new EventDateTime(task.getOffsetDueDate(), "UTC"))
                    .build();

            if (request.start() == null && request.end() == null) {
                log.info("No date changes for event {}", event.getId());
                continue;
            }

            TryResult<Void> result = TryUtils.tryRun(() ->
                    eventClient.patch(
                            user.getCalendarTokenId(),
                            event.getId(),
                            request,
                            params));

            if (result.isSuccess()) {
                log.info("Successfully updated event {}", event.getId());
                continue;
            }

            String message = result.getOptionalException()
                    .map(Throwable::getMessage)
                    .orElse("");

            log.warn("Failed update event {}: {}", event.getId(), message);
        }
    }

    @Override
    public ModificationType getType() {
        return ModificationType.DATE_CHANGE;
    }
}
