package com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findAllByUserEmail(String userEmail);

}
