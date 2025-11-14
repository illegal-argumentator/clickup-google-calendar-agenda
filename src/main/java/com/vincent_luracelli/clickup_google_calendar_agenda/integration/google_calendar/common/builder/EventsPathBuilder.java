package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import org.springframework.web.util.UriComponentsBuilder;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.constants.CalendarPaths.CALENDARS;

public class EventsPathBuilder {

    public static String buildEventByPrimaryCalendarPath(EventParam eventParam) {
        String basePath = CALENDARS.getPath() + "/primary/events";
        return buildEventParamPath(basePath, eventParam);
    }

    public static String buildEventListByPrimaryCalendarPath(EventListParam eventListParam) {
        String basePath = CALENDARS.getPath() + "/primary/events";

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(basePath);

        if (eventListParam.getSearch() != null) {
            uriComponentsBuilder.queryParam("q", eventListParam.getSearch());
        }

        if (eventListParam.getSingleEvents() != null) {
            uriComponentsBuilder.queryParam("singleEvents", eventListParam.getSingleEvents());
        }

        if (eventListParam.getShowDeleted() != null) {
            uriComponentsBuilder.queryParam("showDeleted", eventListParam.getShowDeleted());
        }

        if (eventListParam.getOrderBy() != null) {
            uriComponentsBuilder.queryParam("orderBy", eventListParam.getOrderBy().getOrder());
        }

        if (eventListParam.getMaxResults() != null) {
            uriComponentsBuilder.queryParam("maxResults", eventListParam.getMaxResults());
        }

        if (eventListParam.getPageToken() != null) {
            uriComponentsBuilder.queryParam("pageToken", eventListParam.getPageToken());
        }

        return uriComponentsBuilder.build().encode().toUriString();
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
