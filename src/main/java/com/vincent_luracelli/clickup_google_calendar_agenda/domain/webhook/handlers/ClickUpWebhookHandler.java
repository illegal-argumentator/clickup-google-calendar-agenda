package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventModificationService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {

    private final ClickUpClient clickUpClient;
    private final ClickUpWebhookService clickUpWebhookService;

    private final UserRepository userRepository;
    private final EventModificationService eventOrchestrator;

    public ResponseEntity<String> handleWebhook(String payload, String signature) {
        var req = JsonMapper.fromJson(payload, ClickUpWebhookPayload.class);

        if (req.historyItems().isEmpty()) {
            log.warn("Empty history items received");
            return ResponseEntity.ok("No history items");
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

       if (WebhookEvent.TASK_UPDATED.getEvent().equals(req.event())) {

           Optional<User> webhookUser = userRepository.findByEmail(getWebhookUserEmail(req));
           if (webhookUser.isEmpty()) {
               log.info("Created task does not correspond to APP registered webhook user. Skipping.");
               return ResponseEntity.accepted().build();
           }

           Optional<User> user = userRepository.findByEmail(getTaskCreatorEmail(webhookUser.get(), req));
            if (user.isEmpty()) {
                log.info("Created task does not correspond to APP registered user. Skipping.");
                return ResponseEntity.accepted().build();
            } else {
                return process(user.get(), req);
            }
        }

        return ResponseEntity.ok("OK");
    }

    private ResponseEntity<String> process(User user, ClickUpWebhookPayload req) {
        if (user.getCalendarTokenId() == null) {
            log.warn("User {} has no calendar token", user.getEmail());
            return ResponseEntity.ok("User has no calendar token");
        }

        CompletableFuture.runAsync(() -> {
            try {
                eventOrchestrator.process(user, req);
            } catch (Exception e) {
                log.error("ASYNC FAILED taskId={}", req.taskId(), e);
                throw e;
            }
        });

        return ResponseEntity.ok("OK");
    }

    private String getTaskCreatorEmail(User user, ClickUpWebhookPayload req) {
        Task task = clickUpClient.findTask(user.getClickUpTokenId(), req.taskId());
        return task.getCreator().email();
    }

    private String getWebhookUserEmail(ClickUpWebhookPayload req) {
        return req.historyItems().stream()
                .filter(ClickUpWebhookPayload.HistoryItem::isTaskCreator)
                .map(historyItem -> historyItem.user().email())
                .findFirst().orElseGet(() -> req.historyItems().get(0).user().email());
    }
}
