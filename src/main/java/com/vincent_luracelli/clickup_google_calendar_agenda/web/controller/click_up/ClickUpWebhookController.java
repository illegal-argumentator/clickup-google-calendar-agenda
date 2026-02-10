package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers.ClickUpWebhookHandler;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/click-up/webhook")
public class ClickUpWebhookController {
    private final ClickUpWebhookHandler clickUpWebhookService;

    @Hidden
    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature") String signature) {

        try {
            log.info("Received webhook event. Payload: {}.", payload);
            return clickUpWebhookService.handleWebhook(payload, signature);
        } catch (Exception e) {
            return ResponseEntity.ok().body(e.getMessage());
        }
    }
}
