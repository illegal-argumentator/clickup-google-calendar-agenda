package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventDateUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Option;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarEventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type.TagType.GENODIGDEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateEventsStrategy implements EventActionStrategy {

    private final Cache<String, ReentrantLock> lockCacheManager = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();
    private final EventClient eventClient;
    private final ClickUpClient clickUpClient;
    private final CalendarEventService calendarEventService;
    private final EventRepository eventRepository;

    @Override
    public void execute(User user, ClickUpWebhookPayload payload) {

        List<Event> events =
                eventRepository.findByTaskIdAndUserEmail(payload.taskId(), user.getEmail());

        if (events.isEmpty()) {
            log.warn("No events found for taskId {}", payload.taskId());
        }

        ReentrantLock lock = lockCacheManager.get(user.getId(), k -> new ReentrantLock(true));

        lock.lock();
        try {

            boolean hasTagChange = EventUtils.anyMatchToItems(
                    Set.of("tag_added", "tag"),
                    payload.historyItems()
            );

            boolean hasTagRemoved = EventUtils.anyMatchToItems(
                    Set.of("tag_removed"),
                    payload.historyItems()
            );

            boolean hasEvents = !events.isEmpty();

            if (hasTagChange && !hasEvents) {
                log.info("Creating event for tag update.");
                CompletableFuture.runAsync(
                        () -> createEvent(user, payload, events)
                );
                return;
            }

            if (hasTagRemoved && hasEvents) {
                log.info("Deleting events for taskId {}", payload.taskId());
                CompletableFuture.runAsync(
                        () -> deleteEvent(user, events)
                );
                return;
            }

            updateEvents(user, payload, events);

        } finally {
            lock.unlock();
        }
    }

    private void updateEvents(User user, ClickUpWebhookPayload payload, List<Event> events) {
        EventParam params = EventParam.builder()
                .sendUpdates(EventUpdates.NONE)
                .supportsAttachments(false)
                .build();

        Task task = clickUpClient.findTask(user.getClickUpTokenId(), payload.taskId());

        for (Event event : events) {

            PatchEventRequest request = PatchEventRequest.builder()
                    .start(new EventDateTime(task.getStartDate(), "UTC"))
                    .end(new EventDateTime(task.getDueDate(), "UTC"))
                    .build();

            if (request.start() == null && request.end() == null) {
                log.info("No date changes for event {}", event.getId());
                continue;
            }

            var result = TryUtils.tryRun(() ->
                    eventClient.patch(
                            user.getCalendarTokenId(),
                            event.getId(),
                            request,
                            params
                    )
            );

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

    private void createEvent(User user, ClickUpWebhookPayload payload, List<Event> events) {
        Task task = clickUpClient.findTask(user.getClickUpTokenId(), payload.taskId());
        List<Task> tasks = EventUtils.filterTasksTagByTagName(List.of(task));

        if (!tasks.isEmpty() && events.isEmpty()) {
            EventDateUtils.TaskTimeline taskTime = EventDateUtils.retrieveTaskTimeline(task);

            InsertEventRequest request = InsertEventRequest.builder()
                    .summary("\uD83D\uDCC5 [" + task.getList().getName() + "] " + task.getName())
                    .start(taskTime.start())
                    .taskId(task.getId())
                    .description(task.getDescription())
                    .attendees(getAttendeesEmails(task).stream().map(assignee -> new Attendee(assignee, null)).toList())
                    .end(taskTime.end())
                    .build();

            EventParam eventParam = EventParam.builder().supportsAttachments(true).sendUpdates(EventUpdates.ALL).build();
            calendarEventService.insert(user, eventParam, request);
            return;
        }
        log.info("Couldn't create because task is already created.");
    }

    private List<String> getAttendeesEmails(Task task) {
        try {
            Optional<CustomField> genodigden = task.getCustomFieldByName(GENODIGDEN.getTag());
            return genodigden.map(customField -> customField.typeConfig().options().stream().map(Option::label).toList()).orElseGet(List::of);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return List.of();
    }

    private void deleteEvent(User user, List<Event> events) {

        if (events.isEmpty()) {
            log.info("No events to delete");
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
    public WebhookEvent getEvent() {
        return WebhookEvent.TASK_UPDATED;
    }

}
