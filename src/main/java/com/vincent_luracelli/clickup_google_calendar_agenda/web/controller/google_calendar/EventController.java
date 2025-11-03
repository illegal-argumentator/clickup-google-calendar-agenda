package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.domain.event.service.EventClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.InsertEventParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google-calendar/event")
@RequiredArgsConstructor
public class EventController {

    private final EventClient eventClient;

    @PostMapping
    ResponseEntity<EventResponse> createEvent(
            @Valid InsertEventParam insertEventParam,
            @Valid @RequestBody InsertEventRequest insertEventRequest
    ) {
        EventResponse eventResponse = eventClient.insert(insertEventParam, insertEventRequest);
        return ResponseEntity.ok(eventResponse);
    }

}
