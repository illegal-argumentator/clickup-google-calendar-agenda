package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.type.FieldType.GENODIGDEN;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

    private String id;
    private String name;
    private String description;
    private Status status;
    private Creator creator;
    private Object priority;
    private List<Assignee> assignees;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("date_updated")
    private String dateUpdated;

    @JsonProperty("date_closed")
    private String dateClosed;

    @JsonProperty("date_done")
    private String dateDone;

    private String url;

    private List<Tag> tags;

    private Embedded list;

    private Embedded folder;

    private Object space;

    @JsonProperty("custom_fields")
    private List<CustomField> customFields;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded {

        private String id;

        private String name;

        private boolean access;

    }

    public Optional<CustomField> getCustomFieldByName(String name) {
        return customFields.stream()
                .filter(cf -> cf.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public OffsetDateTime getOffsetStartDate() {
        if (StringUtils.hasText(startDate)) {
            return parseDate(startDate);
        }

        return OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public OffsetDateTime getOffsetDueDate() {
        if (StringUtils.hasText(dueDate)) {

            OffsetDateTime dueDateOffset = parseDate(dueDate);
            if (StringUtils.hasText(startDate)) {

                OffsetDateTime startDateOffset = parseDate(startDate);
                if (startDateOffset.isAfter(dueDateOffset)) {
                    return startDateOffset.plus(Duration.of(30, ChronoUnit.MINUTES));
                }

                return dueDateOffset;
            }
        }

        return getOffsetStartDate().plus(Duration.of(30, ChronoUnit.MINUTES));
    }

    private OffsetDateTime parseDate(String date) {
        if (!StringUtils.hasText(date)) throw new IllegalArgumentException("Date is required.");
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(date)), ZoneOffset.UTC);
    }

    public static List<String> getAttendeesEmails(Task task) {
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
            return List.of();
        }
    }

    public List<String> getTags() {
        return tags.stream()
                .map(Tag::name)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .map(tag -> tag.trim().toLowerCase())
                .toList();

    }
}
