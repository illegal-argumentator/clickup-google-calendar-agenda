package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventClient eventClient;

    public EventResponse insert(EventParam eventParam, InsertEventRequest insertEventRequest) {
        return eventClient.insert(eventParam, insertEventRequest);
    }

    public List<EventResponse> insert(EventParam eventParam, List<InsertEventRequest> insertEventsRequest) {
        List<EventResponse> eventResponses = new ArrayList<>();

        for (InsertEventRequest insertEventRequest : insertEventsRequest) {
            EventResponse eventResponse = insert(eventParam, insertEventRequest);
            eventResponses.add(eventResponse);
        }

        return eventResponses;
    }

    public void patch(String eventId, PatchEventRequest patchEventRequest, EventParam eventParam) {
        eventClient.patch(eventId, patchEventRequest, eventParam);
    }

    public void delete(String eventId, EventParam eventParam) {
        eventClient.delete(eventId, eventParam);
    }


}
