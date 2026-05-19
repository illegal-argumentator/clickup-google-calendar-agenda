package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.EventHelper;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventDateUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Option;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.InsertEventRequest;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.dto.embedded.Attendee;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.common.type.EventUpdates;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.google_calendar.service.CalendarEventService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.google_calendar.dto.EventParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type.TagType.GENODIGDEN;

@Slf4j
@Component
@RequiredArgsConstructor
public final class TagAddedModificationStrategy implements EventModificationStrategy {

    private final CalendarEventService calendarEventService;
    private final EventHelper eventHelper;

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, Task task) {
        if (!EventUtils.hasValidTags(task)) {
            log.info("Delete events for user: {} - found invalid tags.", user.getEmail());
            eventHelper.delete(user, events);
            return;
        }

        EventDateUtils.TaskTimeline taskTime = EventDateUtils.retrieveTaskTimeline(task);
        InsertEventRequest request = InsertEventRequest.builder()
                .summary("\uD83D\uDCC5 [" + task.getList().getName() + "] " + task.getName())
                .start(taskTime.start())
                .taskId(task.getId())
                .description(task.getDescription())
                .attendees(getAttendeesEmails(task).stream().map(assignee -> new Attendee(assignee, null)).toList())
                .end(taskTime.end())
                .build();

        EventParam eventParam = EventParam.builder().supportsAttachments(true).sendUpdates(EventUpdates.ALL).build();
        calendarEventService.insert(user, eventParam, request);

        log.info("Couldn't create because task is already created.");
    }

    private List<String> getAttendeesEmails(Task task) {
        try {
            return task.getCustomFieldByName(GENODIGDEN.getTag())
                    .map(field -> {
                        List<String> selectedIds = field.valueToList();

                        return field.typeConfig()
                                .options()
                                .stream()
                                .filter(option -> selectedIds.contains(option.id()))
                                .map(Option::label)
                                .toList();
                    })
                    .orElse(List.of());

        } catch (Exception e) {
            log.error("Failed to parse attendees", e);
            return List.of();
        }
    }

    @Override
    public ModificationType getType() {
        return ModificationType.TAG_ADD;
    }
}
