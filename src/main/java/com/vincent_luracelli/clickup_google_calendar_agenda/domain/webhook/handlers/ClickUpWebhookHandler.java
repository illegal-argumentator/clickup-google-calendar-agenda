package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.repository.UserRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventModificationService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.repositories.WebhookRepository;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {
    private final ClickUpWebhookService clickUpWebhookService;
    private final WebhookRepository webhookRepository;
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
        var webhook = webhookRepository.findById(req.webhookId())
                .orElseThrow(() -> new RuntimeException("Webhook not found: " + req.webhookId()));
        var userEntity = userRepository.findById(webhook.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + webhook.getUserId()));

        if (userEntity.getCalendarTokenId() == null) {
            log.warn("User {} has no calendar token", userEntity.getEmail());
            return ResponseEntity.ok("User has no calendar token");
        }

        CompletableFuture.runAsync(() -> {
            log.info("ASYNC START taskId={}", req.taskId());

            try {
                eventOrchestrator.process(userEntity, req);
                log.info("ASYNC END taskId={}", req.taskId());
            } catch (Exception e) {
                log.error("ASYNC FAILED taskId={}", req.taskId(), e);
                throw e;
            }
        });

        return ResponseEntity.ok("OK");
    }
}
