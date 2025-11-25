package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service.EventService;
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

    public EventResponse insert(String userEmail, EventParam eventParam, InsertEventRequest insertEventRequest) {
        EventResponse eventResponse = eventClient.insert(userEmail, eventParam, insertEventRequest);
        eventService.save(Event.builder()
                .id(eventResponse.getId())
                .userEmail(userEmail)
                .title(eventResponse.getSummary())
                .build());
        return eventResponse;
    }

    @Transactional
    public List<EventResponse> insert(String userEmail, EventParam eventParam, List<InsertEventRequest> insertEventsRequest) {
        List<EventResponse> eventResponses = new ArrayList<>();

        for (InsertEventRequest insertEventRequest : insertEventsRequest) {
            EventResponse eventResponse = insert(userEmail, eventParam, insertEventRequest);
            eventResponses.add(eventResponse);
        }

        List<Event> events = eventResponses.stream()
                .map(eventResponse -> Event.builder()
                        .id(eventResponse.getId())
                        .title(eventResponse.getSummary())
                        .build()
                )
                .toList();
        eventService.saveAll(events);

        return eventResponses;
    }

    public void patch(String userEmail, String eventId, PatchEventRequest patchEventRequest, EventParam eventParam) {
        eventClient.patch(userEmail, eventId, patchEventRequest, eventParam);
    }

    public void delete(String userEmail, String eventId, EventParam eventParam) {
        eventClient.delete(userEmail, eventId, eventParam);
    }

}
