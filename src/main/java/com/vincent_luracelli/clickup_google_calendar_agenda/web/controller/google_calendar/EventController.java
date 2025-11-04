package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.service.EventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/google-calendar/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    ResponseEntity<EventResponse> createEvent(
            @Valid EventParam eventParam,
            @Valid @RequestBody InsertEventRequest insertEventRequest
    ) {
        EventResponse eventResponse = eventService.insert(eventParam, insertEventRequest);
        return ResponseEntity.ok(eventResponse);
    }

    @PostMapping("/bulk")
    ResponseEntity<List<EventResponse>> createEvents(
            @Valid EventParam eventParam,
            @Valid @RequestBody List<InsertEventRequest> insertEventsRequest
    ) {
        List<EventResponse> eventsResponse = eventService.insert(eventParam, insertEventsRequest);
        return ResponseEntity.ok(eventsResponse);
    }

    @PatchMapping("/{id}")
    void patchEvent(
            @PathVariable String id,
            @Valid @RequestBody PatchEventRequest patchEventRequest,
            @Valid EventParam eventParam
    ) {
        eventService.patch(id, patchEventRequest, eventParam);
    }

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable String id, @Valid EventParam eventParam) {
        eventService.delete(id, eventParam);
    }

}
