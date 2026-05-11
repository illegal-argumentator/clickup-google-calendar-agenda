package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Optional;

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

}
