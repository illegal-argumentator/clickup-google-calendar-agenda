package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.AuthorizeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/oauth")
public class GoogleCalendarOauthController {

    private final CalendarOAuthService calendarOAuthService;

    @GetMapping("/authorize")
    public ResponseEntity<AuthorizeResponse> authorize() {
        AuthorizeResponse authorizeResponse = calendarOAuthService.authorize();

        return ResponseEntity.ok(authorizeResponse);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) {
        calendarOAuthService.callback(code);
    }

}
