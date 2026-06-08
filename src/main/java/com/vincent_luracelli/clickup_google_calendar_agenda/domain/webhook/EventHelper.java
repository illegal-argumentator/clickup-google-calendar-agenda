package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryResult;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventDateUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarEventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField.LOCATION_FIELD;

@Slf4j
@Component
@RequiredArgsConstructor
public final class EventHelper {

    private final EventRepository eventRepository;
    private final CalendarEventService calendarEventService;

    public void delete(User user, List<Event> events) {
        if (events.isEmpty()) {
            log.info("No events to delete.");
            return;
        }

        log.info("Deleting {} events.", events.size());
        EventParam eventParam = EventParam.builder()
                .sendUpdates(EventUpdates.ALL)
                .build();

        events.forEach(event -> {
            eventRepository.deleteById(event.getId());
            calendarEventService.delete(user, event.getId(), eventParam);
        });
    }

    public void create(User user, Task task) {
        EventDateUtils.TaskTimeline taskTime = EventDateUtils.retrieveTaskTimeline(task);
        Optional<CustomField> locationField = task.getCustomFieldByName(LOCATION_FIELD);

        InsertEventRequest request = InsertEventRequest.builder()
                .summary("\uD83D\uDCC5 [" + task.getList().getName() + "] " + task.getName())
                .start(taskTime.start())
                .taskId(task.getId())
                .location(locationField.map(CustomField::getLocationFromField).orElse(null))
                .description(task.getDescription())
                .attendees(Task.getAttendeesEmails(task).stream().map(assignee -> new Attendee(assignee, null)).toList())
                .end(taskTime.end())
                .build();

        EventParam eventParam = EventParam.builder().supportsAttachments(true).sendUpdates(EventUpdates.ALL).build();
        calendarEventService.insert(user, eventParam, request);
    }

    public void update(User user, List<Event> events, PatchEventRequest request) {
        EventParam params = EventParam.builder()
                .sendUpdates(EventUpdates.NONE)
                .supportsAttachments(false)
                .build();

        for (Event event : events) {
            TryResult<Void> result = TryUtils.tryRun(() ->
                    calendarEventService.patch(
                            user,
                            event.getId(),
                            request,
                            params));

            if (result.isSuccess()) {
                log.info("Successfully updated event {}", event.getId());
                continue;
            }

            log.warn("Failed update event {}: {}", event.getId(), result.getOptionalException()
                    .map(Throwable::getMessage)
                    .orElse(""));
        }
    }
}
