package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FieldType {

    GENODIGDEN("genodigden");

    private final String tag;

}
