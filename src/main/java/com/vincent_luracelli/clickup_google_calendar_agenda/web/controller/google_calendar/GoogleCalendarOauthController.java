package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/oauth")
public class GoogleCalendarOauthController {

    private final CalendarOAuthService calendarOAuthService;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws Exception {
        String url = calendarOAuthService.authorize();
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) {
        calendarOAuthService.callback(code);
    }
}
