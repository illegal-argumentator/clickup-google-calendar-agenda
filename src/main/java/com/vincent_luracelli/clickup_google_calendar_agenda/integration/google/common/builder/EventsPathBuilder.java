package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.builder;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.constants.CalendarPaths.CALENDARS;

public class EventsPathBuilder {

    public static String buildGetEventPath(String calendarId, String eventId) {
        return CALENDARS.getPath() + "/%s/events/%s".formatted(calendarId, eventId);
    }

    public static String buildGetEventsPath(String calendarId) {
        return CALENDARS.getPath() + "/%s/events".formatted(calendarId);
    }

}
