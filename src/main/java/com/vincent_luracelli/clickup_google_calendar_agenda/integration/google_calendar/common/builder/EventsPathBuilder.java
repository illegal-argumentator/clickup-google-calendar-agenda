package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.constants.CalendarPaths.CALENDARS;

public class EventsPathBuilder {

    public static String buildEventByCalendarIdPath(String calendarId, EventParam eventParam) {
        String basePath = CALENDARS.getPath() + "/%s/events".formatted(calendarId);
        return buildEventParamPath(basePath, eventParam);
    }

    public static String buildEventListByCalendarIdPath(String calendarId, EventListParam eventListParam) {
        String basePath = CALENDARS.getPath() + "/%s/events".formatted(calendarId);

        StringBuilder sb = new StringBuilder(basePath);

        if (eventListParam.getShowDeleted() == null) {
            sb.append("?showDeleted=false");
        } else {
            sb.append("?showDeleted=%s".formatted(eventListParam.getShowDeleted()));
        }

        if (eventListParam.getOrderBy() != null) {
            sb.append("&orderBy=%s".formatted(eventListParam.getOrderBy().getOrder()));
        }

        if (eventListParam.getTimeZone() != null) {
            sb.append("&timeZone=%s".formatted(eventListParam.getTimeZone()));
        }

        return sb.toString();
    }

    public static String buildEventByIdPath(String eventId, String calendarId, EventParam eventParam) {
        String basePath = CALENDARS.getPath() + "/%s/events/%s".formatted(calendarId, eventId);
        return buildEventParamPath(basePath, eventParam);
    }

    private static String buildEventParamPath(String basePath, EventParam eventParam) {
        StringBuilder sb = new StringBuilder(basePath);

        if (eventParam.getSendUpdates() == null) {
            sb.append("?sendUpdates=false");
        } else {
            sb.append("?sendUpdates=%s".formatted(eventParam.getSendUpdates().getUpdate()));
        }

        if (eventParam.getMaxAttendees() != null) {
            sb.append("&maxAttendees=%d".formatted(eventParam.getMaxAttendees()));
        }

        if (eventParam.getSupportsAttachments() != null) {
            sb.append("&supportsAttachments=%b".formatted(eventParam.getSupportsAttachments()));
        }

        return sb.toString();
    }

}
