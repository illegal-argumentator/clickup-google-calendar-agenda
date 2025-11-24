package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service.EventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFacade {

    private final EventService eventService;

    private final EventClient eventClient;

    public EventListResponse getCreatedEvents(String calendarId, EventListParam eventListParam) {
        EventListResponse eventListResponse = eventClient.list(calendarId, eventListParam);

        Set<String> existingIds = mapAllEventsToIds();
        List<EventResponse> createdEvents = getExistingEventsFromCalendar(existingIds, eventListResponse);
        eventListResponse.setItems(createdEvents);

        log.info("EventOrchestrator: {} - created events, {} - fetched events. Successfully synchronized.", createdEvents.size(), eventListResponse.getItems().size());

        return eventListResponse;
    }

    private Set<String> mapAllEventsToIds() {
        return eventService.findAll().stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
    }

    private List<EventResponse> getExistingEventsFromCalendar(Set<String> existingIds, EventListResponse eventListResponse) {
        return eventListResponse.getItems().stream()
                .filter(event -> existingIds.contains(event.getId()))
                .toList();
    }
}
