package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service.EventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.GetEventResponse;
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
public class EventFacade {

    private final EventService eventService;

    private final EventClient eventClient;

    public EventListResponse getCreatedEvents(User user) {
        List<Event> eventsAllByCalendarTokenId = eventService.findAllByCalendarTokenIdAndUserEmail(user.getCalendarTokenId(), user.getEmail());
        Set<String> existingIds = mapAllEventsToIds(eventsAllByCalendarTokenId);
        List<EventResponse> eventsFromCalendarByCreatedEventIds = getAllEventsByIds(user, existingIds);

        log.info("EventFacade: {} - created events, {} - fetched events for user - {}.", eventsAllByCalendarTokenId.size(), eventsFromCalendarByCreatedEventIds.size(), user.getEmail());

        List<EventResponse> createdEvents = getExistingEventsFromCalendar(existingIds, eventsFromCalendarByCreatedEventIds);

        return EventListResponse.builder()
                .items(createdEvents)
                .build();
    }

    private List<EventResponse> getAllEventsByIds(User user, Set<String> eventIds) {
        List<EventResponse> events = new ArrayList<>();

        for (String eventId : eventIds) {
            try {
                GetEventResponse getEventResponse = eventClient.get(user, eventId);
                events.add(getEventResponse);
            } catch (ApiException exception) {
                log.warn(exception.getMessage());
            }
        }

        return events;
    }

    private Set<String> mapAllEventsToIds(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
    }

    private List<EventResponse> getExistingEventsFromCalendar(Set<String> existingIds, List<EventResponse> eventResponses) {
        return eventResponses.stream()
                .filter(event -> existingIds.contains(event.getId()))
                .toList();
    }
}
