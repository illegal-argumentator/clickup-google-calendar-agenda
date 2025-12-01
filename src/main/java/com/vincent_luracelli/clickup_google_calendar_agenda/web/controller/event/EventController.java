package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.event;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventListResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.service.EventFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventFacade eventFacade;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/all/created")
    ResponseEntity<EventListResponse> getCreatedEvents() {
        User user = jwtUserDetailsService.getUserFromContext();
        return ResponseEntity.ok(eventFacade.getCreatedEvents(user));
    }
}
