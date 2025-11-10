package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.ColorsClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.ColorsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google-calendar/colors")
@RequiredArgsConstructor
public class GoogleCalendarColorsController {

    private final ColorsClient colorsClient;

    @GetMapping
    public ResponseEntity<ColorsResponse> getColors() {
        return ResponseEntity.ok(colorsClient.getColors());
    }
}
