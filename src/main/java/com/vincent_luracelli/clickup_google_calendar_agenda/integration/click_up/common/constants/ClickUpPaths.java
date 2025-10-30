package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.BaseConstants.CLICK_UP_BASE_URL;

@Getter
@AllArgsConstructor
public enum ClickUpPaths {

    TEAMS(CLICK_UP_BASE_URL + "/team");

    private final String path;
}
