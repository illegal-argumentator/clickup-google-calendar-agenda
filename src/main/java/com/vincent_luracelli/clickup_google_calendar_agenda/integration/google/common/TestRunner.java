package com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.EventsClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.EventResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google.common.dto.EventsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final EventsClient eventsClient;

    @Override
    public void run(String... args) {
        EventsResponse all = eventsClient.getAll();
        List<EventResponse> items = all.getItems();
        for (EventResponse item : items) {
            System.out.println(item.getCreator());
        }
    }
}
