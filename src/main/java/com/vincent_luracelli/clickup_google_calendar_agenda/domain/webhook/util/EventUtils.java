package com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.modification.type.ModificationType;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.clickup.ClickUpWebhookPayload;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Tag;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type.TagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class EventUtils {

    public static boolean anyMatchToItems(Set<String> allowed, List<ClickUpWebhookPayload.HistoryItem> historyItems) {
        return historyItems.stream()
                .filter(it -> StringUtils.hasText(it.field()))
                .anyMatch(item -> allowed.contains(item.field()));
    }

    public static List<Task> filterTasksTagByTagName(List<Task> tasks) {
        return tasks.stream().filter(EventUtils::hasValidTags).toList();
    }

    public static boolean hasValidTags(Task task) {
            List<String> tagNames = task.getTags().stream()
                    .map(Tag::name)
                    .filter(io.micrometer.common.util.StringUtils::isNotBlank)
                    .map(tag -> tag.trim().toLowerCase())
                    .toList();

            boolean matches = !tagNames.isEmpty() && tagNames.stream()
                    .allMatch(tagName -> tagName.equals(TagType.BAUSTROM.getTag()) || tagName.equals(TagType.BESTELBON.getTag()));
            log.info("Requested tags: {}. Matches all: {}.", tagNames, matches);
            return matches;
    }

    public static Set<ModificationType> getAllModificationTypes(List<ClickUpWebhookPayload.HistoryItem> historyItems) {
        Set<ModificationType> modifications = new HashSet<>();

        boolean tagAdded = anyMatchToItems(
                Set.of("tag_added", "tag"),
                historyItems
        );
        if (tagAdded) modifications.add(ModificationType.TAG_ADD);

        boolean tagRemoved = anyMatchToItems(
                Set.of("tag_removed"),
                historyItems
        );
        if (tagRemoved) modifications.add(ModificationType.TAG_REMOVE);

        boolean dateChanged = anyMatchToItems(
                Set.of("start_date", "due_date"),
                historyItems
        );
        if (dateChanged) modifications.add(ModificationType.DATE_CHANGE);

        boolean descriptionChanged = anyMatchToItems(
                Set.of("content"),
                historyItems
        );
        if (descriptionChanged) modifications.add(ModificationType.DESCRIPTION_CHANGE);

        boolean customFieldChanged = anyMatchToItems(
                Set.of("custom_field"),
                historyItems
        );
        if (customFieldChanged) modifications.add(ModificationType.CUSTOM_FIELD_CHANGE);

        return modifications;
    }

}
