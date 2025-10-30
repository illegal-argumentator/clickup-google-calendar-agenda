package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Visibility {

    DEFAULT("default"),
    PUBLIC("public"),
    PRIVATE("private"),
    CONFIDENTIAL("confidential");

    private final String visibility;

}
