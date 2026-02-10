package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import com.vincent_luracelli.clickup_google_calendar_agenda.service.WebhookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class EventActionFactory {

    private final List<EventActionStrategy> strategies;

    public EventActionStrategy getStrategy(WebhookEvent event) {
        return strategies.stream().filter(strategy -> strategy.getEvent() == event)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No event action strategy found."));
    }

}
