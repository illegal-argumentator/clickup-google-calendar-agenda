package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.embedded;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderBy {

    START_TIME("startTime"),
    UPDATED("updated");

    private final String order;

}
