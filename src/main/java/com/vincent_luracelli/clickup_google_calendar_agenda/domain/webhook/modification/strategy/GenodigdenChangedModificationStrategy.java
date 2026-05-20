package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.PatchEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenodigdenChangedModificationStrategy implements EventModificationStrategy {

    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, @Nullable Task task) {
        if (events.isEmpty()) {
            log.warn("Couldn't update custom field for: {}, because no events.", user.getEmail());
            return;
        }

        PatchEventRequest.PatchEventRequestBuilder requestBuilder = PatchEventRequest.builder();
        for (CustomField customField : Objects.requireNonNull(task).getCustomFields()) {

            System.out.println(customField);

            if (customField.name().equals(LOCATION_FIELD)) {
                CustomField.Location location = customField.valueToLocation();

                log.info("Updating location: {}, for user: {}.", location, user.getEmail());
                requestBuilder.location(location == null ? null : location.formattedAddress());
            } else if (customField.name().equals(GENODIGDEN_FIELD)) {
                List<String> attendees = customField.selected().stream().map(Selected::label).toList();

                log.info("Updating genodigden: {}, for user: {}.", attendees, user.getEmail());
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
