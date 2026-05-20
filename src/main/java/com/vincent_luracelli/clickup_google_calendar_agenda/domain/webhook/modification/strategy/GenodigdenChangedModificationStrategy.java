package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Option;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenodigdenChangedModificationStrategy implements EventModificationStrategy {

    private static final String LOCATION_FIELD = "Location";
    private static final String GENODIGDEN_FIELD = "genodigden";

    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, @Nullable Task task) {
        if (events.isEmpty()) {
            log.warn("Couldn't update custom field for: {}, because no events.", user.getEmail());
            return;
        }

        PatchEventRequest.PatchEventRequestBuilder requestBuilder = PatchEventRequest.builder();

        for (CustomField customField : Objects.requireNonNull(task).getCustomFields()) {

            if (customField.name().equals(LOCATION_FIELD)) {
                CustomField.Location location = customField.valueToLocation();
                requestBuilder.location(location == null ? null : location.formattedAddress());
            } else if (customField.name().equals(GENODIGDEN_FIELD)) {
                List<String> attendees = customField.typeConfig().options().stream().map(Option::label).toList();
                requestBuilder.attendees(toAttendees(attendees));
            }

        }

        eventHelper.update(user, events, requestBuilder.build());
    }

    private List<Attendee> toAttendees(List<String> attendees) {
        return attendees.stream()
                .map(attendee -> new Attendee(attendee, null))
                .toList();
    }


    @Override
    public ModificationType getType() {
        return ModificationType.CUSTOM_FIELD_CHANGE;
    }
}
