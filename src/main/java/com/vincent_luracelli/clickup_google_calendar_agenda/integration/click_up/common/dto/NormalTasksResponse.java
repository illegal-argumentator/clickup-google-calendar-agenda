package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto;


import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.NormalTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class NormalTasksResponse extends TasksResponse {

    private List<NormalTask> tasks;

}
