package com.vincent_luracelli.clickup_google_calendar_agenda.schedulers;

import com.vincent_luracelli.clickup_google_calendar_agenda.service.ClickUpWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ClickUpWebHookScheduler {
    private final ClickUpWebhookService clickUpWebhookService;

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    private void runScheduler() {
        clickUpWebhookService.setupWebhook();
    }
}
