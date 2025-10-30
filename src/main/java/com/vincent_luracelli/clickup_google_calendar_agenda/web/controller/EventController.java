package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/event")
@RequiredArgsConstructor
public class EventController {

    @PostMapping
    public void createEvent() {

    }

}
