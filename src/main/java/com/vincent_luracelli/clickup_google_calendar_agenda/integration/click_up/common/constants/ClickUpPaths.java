package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.BaseConstants.CLICK_UP_BASE_URL;

@Getter
@AllArgsConstructor
public enum ClickUpPaths {

    OAUTH_TOKEN(CLICK_UP_BASE_URL + "/oauth/token"),
    TEAM(CLICK_UP_BASE_URL + "/team"),
    SPACE(CLICK_UP_BASE_URL + "/space"),
    FOLDER(CLICK_UP_BASE_URL + "/folder"),
    LIST(CLICK_UP_BASE_URL + "/list"),
    WEBHOOK(CLICK_UP_BASE_URL + "/webhook"),
    TASK(CLICK_UP_BASE_URL + "/task");

    private final String path;
}
