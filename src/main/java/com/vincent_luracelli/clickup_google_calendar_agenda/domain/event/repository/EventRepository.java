package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface EventRepository extends MongoRepository<Event, String> {

    void deleteAllByIdIn(Collection<String> ids);
    
}
