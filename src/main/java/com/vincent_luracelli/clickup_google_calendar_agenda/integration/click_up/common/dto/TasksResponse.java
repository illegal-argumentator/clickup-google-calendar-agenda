package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasksResponse {

        private List<Task> tasks;

}
