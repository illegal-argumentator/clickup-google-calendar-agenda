package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {

    NEEDS_ACTION("needsAction"),
    DECLINED("declined"),
    TENTATIVE("tentative"),
    ACCEPTED("accepted");

    private final String status;

}
