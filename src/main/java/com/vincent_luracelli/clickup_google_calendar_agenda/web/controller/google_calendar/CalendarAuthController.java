package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarAuthService;
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

    private final CalendarAuthService calendarAuthService;

    @GetMapping("/me")
    ResponseEntity<MeResponse> me() {
        User user = jwtUserDetailsService.getUserFromContext();
        return ResponseEntity.ok(calendarAuthService.me(user));
    }

}
