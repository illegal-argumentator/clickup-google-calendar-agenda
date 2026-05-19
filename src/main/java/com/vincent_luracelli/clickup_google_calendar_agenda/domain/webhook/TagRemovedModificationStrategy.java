package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarEventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagRemovedModificationStrategy implements EventModificationStrategy {

    private final EventRepository eventRepository;
    private final CalendarEventService calendarEventService;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events) {
        if (events.isEmpty()) {
            log.info("No events to delete.");
            return;
        }

        log.info("Deleting {} events", events.size());

        EventParam eventParam = EventParam.builder()
                .sendUpdates(EventUpdates.ALL)
                .build();

        events.forEach(event -> {
            eventRepository.deleteById(event.getId());
            calendarEventService.delete(user, event.getId(), eventParam);
        });

    }

    @Override
    public ModificationType getType() {
        return ModificationType.TAG_REMOVE;
    }

}
