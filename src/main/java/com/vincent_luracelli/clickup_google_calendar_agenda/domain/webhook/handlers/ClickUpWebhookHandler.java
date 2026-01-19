package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebhookHandler {
    private final ClickUpWebhookService clickUpWebhookService;

    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        var req = JsonMapper.fromJson(payload, ClickUpWebhookPayload.class);
        if (req.historyItems().isEmpty()) {
            log.warn("Empty history items received");
            return ResponseEntity.ok("No history items");
        }
        var secretOpt = clickUpWebhookService.getSecret(req.webhookId());
        if (secretOpt.isEmpty()) {
            log.warn("No secret found for web hook {}", req.webhookId());
            return  ResponseEntity.ok("No secret found for webhookId " + req.webhookId());
        }
        if (!WebhookVerifier.verifySignature(secretOpt.get(), payload, signature)) {
            log.warn("Invalid webhook signature");
            return ResponseEntity.status(401).body("Invalid signature");
        }

        System.out.println("Payload: " + payload);
        System.out.println("signature : " + signature);
        System.out.println("\n\n\n");

        // Обработка разных событий


        return ResponseEntity.ok("OK");
    }
}
