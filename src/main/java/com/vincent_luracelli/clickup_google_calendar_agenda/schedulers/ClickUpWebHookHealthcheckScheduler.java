package com.vincent_luracelli.clickup_google_calendar_agenda.schedulers;

import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpWebHookHealthcheckScheduler {

    private final WebhookHealthService healthService;

    @Scheduled(fixedDelay = 60 * 5000, initialDelay = 60 * 1000)
    private void runScheduler() {
        log.info("Processing webhook healthcheck.");
        healthService.checkAndFix();
    }

}
