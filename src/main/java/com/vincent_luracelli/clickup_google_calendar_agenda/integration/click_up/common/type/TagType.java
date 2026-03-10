package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TagType {

    BAUSTROM("baustrøm"),
    BESTELBON("bestelbon");

    private final String tag;

}
