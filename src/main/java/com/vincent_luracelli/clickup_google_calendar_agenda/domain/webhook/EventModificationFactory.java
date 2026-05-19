package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
final class EventModificationFactory {

    private final List<EventModificationStrategy> strategies;

    public EventModificationStrategy getStrategy(ModificationType type) {
        return strategies.stream()
                .filter(strategy -> strategy.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Modification strategy not found by type: %s.".formatted(type)));
    }
}
