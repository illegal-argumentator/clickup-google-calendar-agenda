package com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityAlreadyExistsException;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.EntityNotFoundException;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.model.CalendarToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.calendar_token.repository.CalendarTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarTokenService {

    private final CalendarTokenRepository calendarTokenRepository;

    public CalendarToken save(CalendarToken calendarToken) {
        Optional<CalendarToken> optionalCalendarToken = findByUserEmail(calendarToken.getUserEmail());

        if (optionalCalendarToken.isPresent()) {
            throw new EntityAlreadyExistsException("Calendar token already exists.");
        }

        return calendarTokenRepository.save(calendarToken);
    }

    public CalendarToken saveOrUpdateIfExists(String userEmail, CalendarToken calendarToken) {
        try {
            return save(calendarToken);
        } catch (EntityAlreadyExistsException e) {
            return update(userEmail, calendarToken);
        }
    }

    public CalendarToken update(String userEmail,CalendarToken updateCalendarToken) {
        CalendarToken calendarToken = calendarTokenRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Calendar token not found."));

        Optional.ofNullable(updateCalendarToken.getAccessToken()).ifPresent(accessToken -> calendarToken.setAccessToken(updateCalendarToken.getAccessToken()));
        Optional.ofNullable(updateCalendarToken.getAccessExpiration()).ifPresent(accessToken -> calendarToken.setAccessExpiration(updateCalendarToken.getAccessExpiration()));
        Optional.ofNullable(updateCalendarToken.getRefreshToken()).ifPresent(accessToken -> calendarToken.setRefreshToken(updateCalendarToken.getRefreshToken()));
        Optional.ofNullable(updateCalendarToken.getUserEmail()).ifPresent(accessToken -> calendarToken.setUserEmail(updateCalendarToken.getUserEmail()));

        return calendarTokenRepository.save(calendarToken);
    }

    public Optional<CalendarToken> findByUserEmail(String userEmail) {
        return calendarTokenRepository.findByUserEmail(userEmail);
    }

    public Optional<CalendarToken> findById(String id) {
        return calendarTokenRepository.findById(id);
    }
}
