package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventActionFactory;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories.WebhookRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {
    private final ClickUpWebhookService clickUpWebhookService;
    private final EventRepository eventRepository;
    private final WebhookRepository webhookRepository;
    private final UserRepository userRepository;
    private final EventActionFactory eventActionFactory;

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

        log.info("Processing webhook for req {}", req);
        var webhookEntity = webhookRepository.findById(req.webhookId()).orElseThrow();
        var userEntity = userRepository.findById(webhookEntity.getUserId()).orElseThrow();
        if (userEntity.getCalendarTokenId() == null) {
            log.warn("User {} has no calendar token", userEntity.getEmail());
            return ResponseEntity.ok("User has no calendar token");
        }

        CompletableFuture.runAsync(() -> eventActionFactory.getStrategy(extractEventFrom(req.event())).execute(userEntity, req))
                .thenRun(() -> log.info("Successfully updated events for taskId {}", req.taskId()))
                .exceptionally(ex -> {
                    log.error("Error updating events for taskId {}", req.taskId(), ex);
                    return null;
                });

        return ResponseEntity.ok("OK");
    }

    private boolean isValidWebhook(ClickUpWebhookPayload payload) {
        boolean eventNotMatched = Arrays.stream(WebhookEvent.values())
                .noneMatch(event -> event.getEvent().equals(payload.event()));
        if (eventNotMatched) {
            return false;
        }

        if (payload.event().equals(WebhookEvent.TASK_CREATED.getEvent())) {
            return true;
        }

        var fields = Set.of("start_date", "due_date");
        return payload.historyItems().stream()
                .filter(it -> StringUtils.hasText(it.field()))
                .anyMatch(item -> fields.contains(item.field()));

    }

    private static WebhookEvent extractEventFrom(String event) {
        return Arrays.stream(WebhookEvent.values())
                .filter(webhookEvent -> webhookEvent.getEvent().equals(event))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No webhook event found."));
    }

}
