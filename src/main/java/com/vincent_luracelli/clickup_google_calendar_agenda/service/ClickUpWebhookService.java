package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Option;

import java.util.Optional;

public interface ClickUpWebhookService {

    Optional<String> getSecret(String email);

    void setupWebhook(String... userIds);

    void setupWebhook();
}
