package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WebhookHealth {

    FAILING("failing"),
    ACTIVE("active");

    private final String health;

    public static boolean isActive(String status) {
        return ACTIVE.getHealth().equals(status);
    }
}
