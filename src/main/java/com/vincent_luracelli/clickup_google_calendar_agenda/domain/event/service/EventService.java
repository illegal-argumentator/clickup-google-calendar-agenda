package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> findAllByUserEmailAndCalendarTokenId(String userEmail, String calendarTokenId) {
        return eventRepository.findAllByUserEmailAndCalendarTokenId(userEmail, calendarTokenId);
    }

    public void save(Event event) {
        eventRepository.save(event);
    }

    public void saveAll(List<Event> events) {
        eventRepository.saveAll(events);
    }
}
