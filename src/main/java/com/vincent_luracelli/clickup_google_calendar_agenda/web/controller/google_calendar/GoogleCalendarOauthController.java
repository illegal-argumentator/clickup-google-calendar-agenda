package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.dto.ResponsePayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/oauth")
public class GoogleCalendarOauthController {

    private final CalendarOAuthService calendarOAuthService;

    @GetMapping("/authorize")
    public ResponseEntity<ResponsePayload> authorize(HttpServletResponse response) throws Exception {
        String url = calendarOAuthService.authorize();
        return ResponseEntity.ok(ResponsePayload.builder()
                        .code(HttpStatus.OK.value())
                        .source(SourceType.API)
                        .body(url)
                .build());
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) {
        calendarOAuthService.callback(code);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponsePayload> me() {
        calendarOAuthService.me();
        return ResponseEntity.ok(ResponsePayload.builder()
                .body("Authorized.")
                .source(SourceType.GOOGLE_CALENDAR)
                .code(HttpStatus.OK.value()).build());
    }
}
