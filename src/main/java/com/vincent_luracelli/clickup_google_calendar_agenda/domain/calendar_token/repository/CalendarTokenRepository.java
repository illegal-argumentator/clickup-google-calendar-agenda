package com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.repository;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CalendarTokenRepository extends MongoRepository<CalendarToken, String> {

    Optional<CalendarToken> findByUserEmail(String userEmail);

}
