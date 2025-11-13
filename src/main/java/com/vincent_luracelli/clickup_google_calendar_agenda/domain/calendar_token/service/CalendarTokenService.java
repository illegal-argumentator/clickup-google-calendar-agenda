package com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.repository.CalendarTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarTokenService {

    private final CalendarTokenRepository calendarTokenRepository;

    public void save(CalendarToken calendarToken) {
        findByCalendarId(calendarToken.getCalendarId())
                .map(existing -> {
                    calendarToken.setId(existing.getId());
                    return calendarTokenRepository.save(calendarToken);
                })
                .orElseGet(() -> calendarTokenRepository.save(calendarToken));
    }

    public Optional<CalendarToken> findByCalendarId(String calendarId) {
        return calendarTokenRepository.findByCalendarId(calendarId);
    }

    public void deleteByCalendarId(String calendarId) {
        calendarTokenRepository.deleteByCalendarId(calendarId);
    }

}
