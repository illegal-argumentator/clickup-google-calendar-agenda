package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
