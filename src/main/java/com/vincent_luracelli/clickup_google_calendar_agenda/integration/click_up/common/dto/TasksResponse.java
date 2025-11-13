package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;

import java.util.List;

public class TasksResponse {

        private List<Task> tasks;

        @JsonProperty("last_page")
        private boolean lastPage;

}
