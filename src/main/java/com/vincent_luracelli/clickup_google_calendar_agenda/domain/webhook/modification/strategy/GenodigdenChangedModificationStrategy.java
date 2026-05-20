package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.strategy;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.event.model.Event;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.CustomField;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GenodigdenChangedModificationStrategy implements EventModificationStrategy {

    @Override
    public void modify(User user, ClickUpWebhookPayload payload, List<Event> events, @Nullable Task task) {
        List<CustomField> customFields = task.getCustomFields();
        System.out.println(customFields);
    }

    @Override
    public ModificationType getType() {
        return ModificationType.CUSTOM_FIELD_CHANGE;
    }
}
