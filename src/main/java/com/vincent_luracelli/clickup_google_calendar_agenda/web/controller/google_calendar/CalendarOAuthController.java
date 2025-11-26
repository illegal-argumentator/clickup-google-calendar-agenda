package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/oauth")
public class CalendarOAuthController {

    private final CalendarOAuthService calendarOAuthService;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/authorize")
    ResponseEntity<AuthorizeResponse> authorize() {
        AuthorizeResponse authorizeResponse = calendarOAuthService.authorize();
        return ResponseEntity.ok(authorizeResponse);
    }

    @PostMapping("/callback")
    void callback(@RequestParam String code) {
        User user = jwtUserDetailsService.getUserFromContext();
        calendarOAuthService.callback(code, user);
    }

}
