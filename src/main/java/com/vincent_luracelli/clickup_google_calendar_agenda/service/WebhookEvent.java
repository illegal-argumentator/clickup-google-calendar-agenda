package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WebhookEvent {

    TASK_UPDATED("taskUpdated"),
    TASK_CREATED("taskCreated");

    private final String event;

    public boolean nameOf(String name) {
        try {
            WebhookEvent.valueOf(name);
            return true;
        } catch (Exception e) {
            System.out.printf("ex", e);
            return false;
        }
    }


}
