package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.ThreadUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.tries.TryUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories.WebhookRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.EventDateTime;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {
    private final Cache<String, ReentrantLock> lockCacheManager = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();
    private final ClickUpWebhookService clickUpWebhookService;
    private final EventRepository eventRepository;
    private final WebhookRepository webhookRepository;
    private final UserRepository userRepository;
    private final EventClient eventClient;

    public ResponseEntity<String> handleWebhook(String payload, String signature) {

        var req = JsonMapper.fromJson(payload, ClickUpWebhookPayload.class);
        if (req.historyItems().isEmpty()) {
            log.warn("Empty history items received");
            return ResponseEntity.ok("No history items");
        }
        if (!isValidWebhook(req)) {
            log.info("Ignoring irrelevant webhook event: {}", req.event());
            return ResponseEntity.ok("Irrelevant event");
        }

        var secretOpt = clickUpWebhookService.getSecret(req.webhookId());
        if (secretOpt.isEmpty()) {
            log.warn("No secret found for web hook {}", req.webhookId());
            return ResponseEntity.ok("No secret found for webhookId " + req.webhookId());
        }
        if (!WebhookVerifier.verifySignature(secretOpt.get(), payload, signature)) {
            log.warn("Invalid webhook signature");
            return ResponseEntity.status(401).body("Invalid signature");
        }

        var webhookEntity = webhookRepository.findById(req.webhookId()).orElseThrow();
        var userEntity = userRepository.findById(webhookEntity.getUserId()).orElseThrow();
        if (userEntity.getCalendarTokenId() == null) {
            log.warn("User {} has no calendar token", userEntity.getEmail());
            return ResponseEntity.ok("User has no calendar token");
        }
        var events = eventRepository.findByTaskIdAndUserEmail(req.taskId(), userEntity.getEmail());
        if (events.isEmpty()) {
            log.warn("No events found for taskId {}", req.taskId());
            return ResponseEntity.ok("No events found for taskId " + req.taskId());
        }

        CompletableFuture.runAsync(() -> updateEvents(events, userEntity, req))
                .thenRun(() -> log.info("Successfully updated events for taskId {}", req.taskId()))
                .exceptionally(ex -> {
                    log.error("Error updating events for taskId {}", req.taskId(), ex);
                    return null;
                });

        return ResponseEntity.ok("OK");
    }

    private boolean isValidWebhook(ClickUpWebhookPayload payload) {
        if (!"taskUpdated".equals(payload.event())) {
            return false;
        }
        var fields = Set.of("start_date", "due_date");
        return payload.historyItems().stream()
                .filter(it -> StringUtils.hasText(it.field()))
                .anyMatch(item -> fields.contains(item.field()));

    }

    private void updateEvents(List<Event> events, User user, ClickUpWebhookPayload payload) {
        var lock = lockCacheManager.get(user.getId(), k -> new ReentrantLock());
        lock.lock();
        try {
            for (Event event : events) {
                PatchEventRequest request = PatchEventRequest.builder()
                        .start(getStartDate(payload))
                        .end(getEndDate(payload))
                        .build();
                if (request.start() == null && request.end() == null) {
                    log.info("No start or end date changes for event {}", event.getId());
                    continue;
                }

                var params = EventParam.builder()
                        .sendUpdates(EventUpdates.ALL)
                        .supportsAttachments(true)
                        .build();

                eventClient.patch(user, event.getId(), request, params);
            }
        } finally {
            lock.unlock();
        }
    }

    private EventDateTime getStartDate(ClickUpWebhookPayload payload) {
        var historyItemOpt = payload.historyItems().stream()
                .filter(it -> "start_date".equals(it.field()))
                .findFirst();
        if (historyItemOpt.isEmpty()) {
            return null;
        }
        return getEventDateTime(historyItemOpt);
    }

    private EventDateTime getEndDate(ClickUpWebhookPayload payload) {
        var historyItemOpt = payload.historyItems().stream()
                .filter(it -> "due_date".equals(it.field()))
                .findFirst();
        if (historyItemOpt.isEmpty()) {
            return null;
        }
        return getEventDateTime(historyItemOpt);
    }

    private EventDateTime getEventDateTime(Optional<ClickUpWebhookPayload.HistoryItem> historyItemOpt) {
        var historyItem = historyItemOpt.orElseThrow();
        if (!StringUtils.hasText(historyItem.after())) {
            return null;
        }
        try {
            var dateTime = Long.parseLong(historyItem.after());
            var instant = java.time.Instant.ofEpochMilli(dateTime);
            var offsetDateTime = java.time.OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
            return new EventDateTime(offsetDateTime, "UTC");
        }catch (Exception e){
            log.warn("Failed to parse date time from history item: {}", historyItem.after(), e);
            return null;
        }
    }
}
