package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.InsertEventParam;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.constants.CalendarPaths.CALENDARS;

public class EventsPathBuilder {

    public static String buildEventByCalendarIdPath(String calendarId, InsertEventParam insertEventParam) {
         String basePath = CALENDARS.getPath() + "/%s/events".formatted(calendarId);

        StringBuilder sb = new StringBuilder(basePath);

        if (insertEventParam.getSendUpdates() == null) {
            sb.append("?sendUpdates=false");
        } else {
            sb.append("?sendUpdates=%s".formatted(insertEventParam.getSendUpdates().getUpdate()));
        }

        if (insertEventParam.getMaxAttendees() != null) {
            sb.append("&maxAttendees=%d".formatted(insertEventParam.getMaxAttendees()));
        }

        if (insertEventParam.getSupportsAttachments() != null) {
            sb.append("&supportsAttachments=%b".formatted(insertEventParam.getSupportsAttachments()));
        }

        return sb.toString();
    }

    public static String buildUpdateEventPath(String eventId, String calendarId) {
        return CALENDARS.getPath() + "/%s/events/%s".formatted(calendarId, eventId);
    }

}
