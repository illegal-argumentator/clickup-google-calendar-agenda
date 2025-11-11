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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventOrchestrator {

    private final EventService eventService;

    private final EventClient eventClient;

    public EventListResponse syncCreatedEvents(EventListParam eventListParam) {
        EventListResponse eventListResponse = eventClient.list(eventListParam);
        List<String> deletedEventIds = new ArrayList<>();

        Set<String> existingIds = eventService.findAll().stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        List<EventResponse> createdEvents = eventListResponse.getItems().stream()
                .filter(event -> {
                    boolean contains = existingIds.contains(event.getId());
                    if (!contains) {
                        deletedEventIds.add(event.getId());
                    }
                    return contains;
                })
                .toList();

        eventService.deleteAllByIds(deletedEventIds);
        eventListResponse.setItems(createdEvents);

        log.info("EventOrchestrator: {} - created events, {} - deleted events. Successfully synchronized.", createdEvents.size(), deletedEventIds.size());

        return eventListResponse;
    }
}
