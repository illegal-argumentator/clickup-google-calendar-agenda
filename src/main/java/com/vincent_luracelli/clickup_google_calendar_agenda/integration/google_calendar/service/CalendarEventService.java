package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service.EventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final EventClient eventClient;

    private final EventService eventService;

    public EventResponse insert(User user, EventParam eventParam, InsertEventRequest insertEventRequest) {
        EventResponse eventResponse = eventClient.insert(user.getCalendarTokenId(), eventParam, insertEventRequest);
        eventService.save(Event.builder()
                .id(eventResponse.getId())
                .userEmail(user.getEmail())
                .taskId(insertEventRequest.taskId())
                .title(eventResponse.getSummary())
                .calendarTokenId(user.getCalendarTokenId())
                .attendees(insertEventRequest.attendees())
                .build());
        return eventResponse;
    }

    @Transactional
    public List<EventResponse> insert(User user, EventParam eventParam, List<InsertEventRequest> insertEventsRequest) {
        List<EventResponse> eventResponses = new ArrayList<>();

        for (InsertEventRequest insertEventRequest : insertEventsRequest) {
            EventResponse eventResponse = insert(user, eventParam, insertEventRequest);
            eventResponses.add(eventResponse);
        }

        return eventResponses;
    }

    public void patch(User user, String eventId, PatchEventRequest patchEventRequest, EventParam eventParam) {
        eventClient.patch(user.getCalendarTokenId(), eventId, patchEventRequest, eventParam);
    }

    public void delete(User user, String eventId, EventParam eventParam) {
        eventClient.delete(user, eventId, eventParam);
    }

}
