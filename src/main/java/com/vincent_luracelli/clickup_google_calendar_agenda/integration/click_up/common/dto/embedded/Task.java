package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Task (
        String id,

        String name,
        String description,
        Status status,

        Creator creator,
        List<Assignee> assignees,

        @JsonProperty("start_date")
        String startDate,

        @JsonProperty("due_date")
        String dueDate,

        String url,

        @JsonProperty("custom_fields")
        List<CustomField> customFields
) {
}
