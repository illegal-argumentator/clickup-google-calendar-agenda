package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service.EventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFacade {

    private final EventService eventService;

    private final EventClient eventClient;

    private record EventListRecord(
            List<EventResponse> items,
            String nextPageToken
    ){}

    public EventListResponse getCreatedEvents(User user, EventListParam eventListParam) {
        // google calendar api bug fix
        eventListParam.setMaxResults(9999);

        EventListResponse eventListResponse = new EventListResponse();
        eventListResponse.setItems(new ArrayList<>());

        for (var isSingleEvents : new boolean[]{false, true}) {
            eventListParam.setSingleEvents(isSingleEvents);
            var fetchedEvents = fetchCreatedEvents(user, eventListParam);
            eventListResponse.getItems().addAll(fetchedEvents.items());
            if (isSingleEvents){
                eventListResponse.setSingleEventNextPageToken(fetchedEvents.nextPageToken());
            } else {
                eventListResponse.setNextPageToken(fetchedEvents.nextPageToken());
            }
        }

        Set<String> ids = new HashSet<>();
        eventListResponse.getItems().removeIf(event -> !ids.add(event.getId()));

        return eventListResponse;
    }

    private EventListRecord fetchCreatedEvents(User user, EventListParam eventListParam){


        EventListResponse eventListResponse = eventClient.list(user, eventListParam);
        List<Event> eventsAllByCalendarTokenId = eventService.findAllByCalendarTokenIdAndUserEmail(user.getCalendarTokenId(), user.getEmail());
//        System.out.println("Created events: " + eventsAllByCalendarTokenId);
//        log.info("EventFacade: {} - created events, {} - fetched events for user - {}.", eventsAllByCalendarTokenId.size(), eventListResponse.getItems().size(), user.getEmail());

        Set<String> existingIds = mapAllEventsToIds(eventsAllByCalendarTokenId);
        List<EventResponse> createdEvents = getExistingEventsFromCalendar(existingIds, eventListResponse);
        return new EventListRecord(createdEvents, eventListResponse.getNextPageToken());
    }

    private Set<String> mapAllEventsToIds(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
    }

    private List<EventResponse> getExistingEventsFromCalendar(Set<String> existingIds, EventListResponse eventListResponse) {
//        System.out.println("events from calendar: " + eventListResponse.getItems().stream().map(EventResponse::getId).toList());
        return eventListResponse.getItems().stream()
                .filter(event -> existingIds.contains(event.getId()))
                .toList();
    }
}