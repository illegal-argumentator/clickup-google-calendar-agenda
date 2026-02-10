package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Folder;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Reminders;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateEventsStrategy implements EventActionStrategy {

    private final Cache<String, ReentrantLock> lockCacheManager = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    private final EventClient eventClient;

    private final ClickUpClient clickUpClient;

    private final EventRepository eventRepository;

    @Override
    public void execute(User user, ClickUpWebhookPayload payload) {
        var lock = lockCacheManager.get(user.getId(), k -> new ReentrantLock(true));
        lock.lock();

        try {
            EventParam eventParam = EventParam.builder()
                    .sendUpdates(EventUpdates.ALL)
                    .supportsAttachments(true)
                    .build();

            Task task = clickUpClient.findTask(user.getClickUpTokenId(), payload.taskId());
            Folder folder = clickUpClient.findFolder(user.getClickUpTokenId(), task.getFolder().getId());
            // folder={id=90127484820, name=hidden, hidden=true, access=true}
            log.info("Task: {}", task);
            log.info("Folder: {}", folder);
            eventClient.insert(user.getCalendarTokenId(), eventParam, mapToEventRequest(task));
            eventRepository.save(Event.builder()
                            .title(task.getName())
                            .taskId(task.getId())
                            .userEmail(user.getEmail())
                            .calendarTokenId(user.getCalendarTokenId())
                    .build());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.TASK_CREATED;
    }

    private InsertEventRequest mapToEventRequest(Task task) {
        EventDateTime start = EventDateTime.builder()
                .dateTime(task.getStartDate() == null
                        ? OffsetDateTime.now(ZoneOffset.UTC)
                        : parseTimestamp(task.getStartDate()))
                .timeZone("UTC")
                .build();

        EventDateTime end = EventDateTime.builder()
                .dateTime(task.getDueDate() == null
                        ? OffsetDateTime.now(ZoneOffset.UTC)
                        : parseTimestamp(task.getDueDate()))
                .timeZone("UTC")
                .build();

        List<Attendee> attendees = task.getAssignees().stream()
                .map(assignee -> Attendee.builder().email(assignee.email()).build())
                .toList();

        return InsertEventRequest.builder()
                .start(start)
                .end(end)
                .summary(task.getName())
                .description(task.getDescription())
                .attendees(attendees)
                .reminders(Reminders.builder().useDefault(true).build())
                .taskId(task.getId())
                .build();
    }

    private OffsetDateTime parseTimestamp(String value) {
        return OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(value)),
                ZoneOffset.UTC
        );
    }

}
