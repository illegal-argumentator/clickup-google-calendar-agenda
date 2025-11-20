package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.event;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.EventOrchestrator;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventListParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventOrchestrator eventOrchestrator;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/all/created")
    ResponseEntity<EventListResponse> getCreatedEvents(EventListParam eventListParam) {
        String username = jwtUserDetailsService.retrieveUserDetailsFromContext().getUsername();
        return ResponseEntity.ok(eventOrchestrator.retrieveCreatedEvents(username, eventListParam));
    }
}
