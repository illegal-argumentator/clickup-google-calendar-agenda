package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import java.util.Optional;

public interface ClickUpWebhookService {

    Optional<String> getSecret(String webhookId);

    void setupWebhook(String... userIds);

    void setupWebhook();
}
