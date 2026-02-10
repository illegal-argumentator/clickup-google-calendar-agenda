package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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

    private final EventRepository eventRepository;

    @Override
    public void execute(User user, ClickUpWebhookPayload payload) {
        var events = eventRepository.findByTaskIdAndUserEmail(payload.taskId(), user.getEmail());
        if (events.isEmpty()) {
            log.warn("No events found for taskId {}", payload.taskId());
        }

        var lock = lockCacheManager.get(user.getId(), k -> new ReentrantLock(true));
        lock.lock();

        try {
            for (Event event : events) {
                PatchEventRequest request = PatchEventRequest.builder()
                        .start(EventUtils.getStartDate(payload))
                        .end(EventUtils.getEndDate(payload))
                        .summary(event.getTitle())
                        .attendees(event.getAttendees())
                        .build();

                if (request.start() == null && request.end() == null) {
                    log.info("No start or end date changes for event {}", event.getId());
                    continue;
                }

                var params = EventParam.builder()
                        .sendUpdates(EventUpdates.ALL)
                        .supportsAttachments(true)
                        .build();

                var result = TryUtils.tryRun(() -> eventClient.patch(user.getCalendarTokenId(), event.getId(), request, params));
                if (result.isSuccess()) {
                    log.info("Successfully updated event {}", event.getId());
                    continue;
                }
                var message = result.getOptionalException().map(Throwable::getMessage)
                        .filter(StringUtils::hasText)
                        .orElse("");
                if (!message.contains("The specified time range is empty")) {
                    log.warn("New error {}", event.getId(), result.exception());
                    continue;
                }
                ArrayList<EventDateTime> dateTimes = new ArrayList<>();
                dateTimes.add(request.start());
                dateTimes.add(request.end());
                dateTimes.removeIf(Objects::isNull);
                if (dateTimes.isEmpty()) {
                    log.warn("No events found for event {}", event.getId());
                    continue;
                }

                var end = dateTimes.get(0).dateTime().plusHours(1);

                PatchEventRequest newRangeReq = PatchEventRequest.builder()
                        .start(dateTimes.get(0))
                        .end(new EventDateTime(end, "UTC"))
                        .build();
                log.info("New event range request {}", newRangeReq);
                var newRangeResult = TryUtils.tryRun(() -> eventClient.patch(user.getCalendarTokenId(), event.getId(), newRangeReq, params));
                newRangeResult.onFail(ex -> log.error("Error updating events for taskId {}", event.getId(), ex));
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WebhookEvent getEvent() {
        return WebhookEvent.TASK_UPDATED;
    }

}
