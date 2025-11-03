package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EventUpdates {

    ALL("all"),
    EXTERNAL_ONLY("externalOnly"),
    NONE("none");

    private final String update;

}
