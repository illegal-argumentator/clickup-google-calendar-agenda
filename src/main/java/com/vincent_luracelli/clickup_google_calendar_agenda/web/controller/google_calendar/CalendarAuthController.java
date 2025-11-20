package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarAuthTokenService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/auth")
public class CalendarAuthController {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final CalendarAuthTokenService calendarAuthTokenService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        String username = jwtUserDetailsService.retrieveUserDetailsFromContext().getUsername();
        return ResponseEntity.ok(calendarAuthTokenService.me(username));
    }

}
