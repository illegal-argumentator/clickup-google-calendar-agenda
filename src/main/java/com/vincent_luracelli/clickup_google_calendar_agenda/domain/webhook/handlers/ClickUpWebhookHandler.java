package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventModificationService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.entities.WebhookEntity;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories.WebhookRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {

    private final ClickUpClient clickUpClient;
    private final ClickUpWebhookService clickUpWebhookService;

    private final UserRepository userRepository;
    private final WebhookRepository webhookRepository;
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
           Optional<WebhookEntity> webhookEntity = webhookRepository.findById(req.webhookId());
           if (webhookEntity.isEmpty()) {
               log.info("Webhook {} is not registered in the application. Skipping.", req.webhookId());
               return ResponseEntity.accepted().build();
           }

           Optional<User> webhookOwner = userRepository.findById(webhookEntity.get().getUserId());
           if (webhookOwner.isEmpty()) {
               log.info("Owner {} of webhook {} is not registered in the application. Skipping.",
                       webhookEntity.get().getUserId(), req.webhookId());
               return ResponseEntity.accepted().build();
           }

           User owner = webhookOwner.get();
           String creatorEmail = getTaskCreatorEmail(owner, req);
           Optional<User> taskCreator = userRepository.findByEmail(creatorEmail);
           if (taskCreator.isEmpty()) {
               log.info("Task creator {} is not registered in the application. Skipping.", creatorEmail);
               return ResponseEntity.accepted().build();
           }

           if (!Objects.equals(owner.getId(), taskCreator.get().getId())) {
               log.info("Webhook owner {} is not creator of task {} (creator: {}). Skipping.",
                       owner.getEmail(), req.taskId(), taskCreator.get().getEmail());
               return ResponseEntity.accepted().build();
           }

           return process(owner, req);
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
}
