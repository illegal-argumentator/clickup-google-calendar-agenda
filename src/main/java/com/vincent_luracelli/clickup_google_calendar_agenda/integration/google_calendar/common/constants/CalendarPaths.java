package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.constants.BaseConstants.CALENDAR_BASE_URL;

@Getter
@AllArgsConstructor
public enum CalendarPaths {

    CALENDARS(CALENDAR_BASE_URL + "/calendars");

    private final String path;

}
