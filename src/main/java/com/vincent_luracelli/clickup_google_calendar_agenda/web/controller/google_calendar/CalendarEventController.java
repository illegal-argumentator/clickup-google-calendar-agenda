package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarEventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/google-calendar/event")
@RequiredArgsConstructor
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    private final JwtUserDetailsService jwtUserDetailsService;

    @PostMapping
    ResponseEntity<EventResponse> createEvent(
            @Valid EventParam eventParam,
            @Valid @RequestBody InsertEventRequest insertEventRequest
    ) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        EventResponse eventResponse = calendarEventService.insert(username, eventParam, insertEventRequest);
        return ResponseEntity.ok(eventResponse);
    }

    @PostMapping("/bulk")
    ResponseEntity<List<EventResponse>> createEvents(
            @Valid EventParam eventParam,
            @Valid @RequestBody List<InsertEventRequest> insertEventsRequest
    ) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        List<EventResponse> eventsResponse = calendarEventService.insert(username, eventParam, insertEventsRequest);
        return ResponseEntity.ok(eventsResponse);
    }

    @PatchMapping("/{id}")
    void patchEvent(
            @PathVariable String id,
            @Valid @RequestBody PatchEventRequest patchEventRequest,
            @Valid EventParam eventParam
    ) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        calendarEventService.patch(username, id, patchEventRequest, eventParam);
    }

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable String id, @Valid EventParam eventParam) {
        String username = jwtUserDetailsService.getUserDetailsFromContextOrThrow().getUsername();
        calendarEventService.delete(username, id, eventParam);
    }

}
