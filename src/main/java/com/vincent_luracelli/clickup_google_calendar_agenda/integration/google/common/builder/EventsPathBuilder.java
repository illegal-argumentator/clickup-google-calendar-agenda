package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.builder;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.constants.CalendarPaths.CALENDARS;

public class EventsPathBuilder {

    public static String buildEventByCalendarIdPath(String calendarId) {
        return CALENDARS.getPath() + "/%s/events".formatted(calendarId);
    }

    public static String buildUpdateEventPath(String eventId, String calendarId) {
        return CALENDARS.getPath() + "/%s/events/%s".formatted(calendarId, eventId);
    }

}
