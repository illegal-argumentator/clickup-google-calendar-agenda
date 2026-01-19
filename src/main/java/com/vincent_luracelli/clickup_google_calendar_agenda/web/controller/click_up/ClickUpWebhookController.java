package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clickup/webhook")
public class ClickUpWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        String event = (String) payload.get("event");
//        Map<String, Object> taskData = (Map<String, Object>) payload.get("task_id");

        System.out.println("Получено событие: " + event);
        System.out.println("Payload: " + payload);
        System.out.println("signature : " + signature);

        // Обработка разных событий


        return ResponseEntity.ok("OK");
    }
}
