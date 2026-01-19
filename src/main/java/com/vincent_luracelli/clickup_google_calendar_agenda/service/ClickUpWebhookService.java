package com.vincent_luracelli.clickup_google_calendar_agenda.service;

public interface ClickUpWebhookService {

    void setupWebhook(String... userIds);

    void setupWebhook();
}
