package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.BaseConstants.CLICK_UP_BASE_URL;

@Getter
@AllArgsConstructor
public enum ClickUpPaths {

    TEAM(CLICK_UP_BASE_URL + "/team"),
    SPACE(CLICK_UP_BASE_URL + "/space"),
    FOLDER(CLICK_UP_BASE_URL + "/folder"),
    LIST(CLICK_UP_BASE_URL + "/list");

    private final String path;
}
