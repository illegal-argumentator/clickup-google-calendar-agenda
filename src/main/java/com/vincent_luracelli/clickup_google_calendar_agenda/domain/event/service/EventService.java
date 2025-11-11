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

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public void save(Event event) {
        eventRepository.save(event);
    }

    public void saveAll(List<Event> events) {
        eventRepository.saveAll(events);
    }

    public void deleteAllByIds(List<String> ids) {
        eventRepository.deleteAllByIdIn(ids);
    }
}
