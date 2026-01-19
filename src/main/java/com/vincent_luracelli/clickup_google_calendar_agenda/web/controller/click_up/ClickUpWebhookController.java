package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.JsonMapper;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.WebhookVerifier;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.handlers.ClickUpWebhookHandler;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
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
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        return clickUpWebhookService.handleWebhook(payload, signature);
    }
}
