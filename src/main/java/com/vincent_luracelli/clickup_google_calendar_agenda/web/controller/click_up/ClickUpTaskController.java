package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.FilteredTasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.NormalTasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpTaskController {

    private final ClickUpClient clickUpClient;

    @GetMapping("/list/{id}/task")
    ResponseEntity<NormalTasksResponse> findTasksByList(@PathVariable String id) {
        return ResponseEntity.ok(clickUpClient.findTasksByList(id));
    }

    @GetMapping("/team/{id}/task")
    ResponseEntity<FilteredTasksResponse> findFilteredTaskByTeam(
            @PathVariable String id,
            TaskFilterParam taskFilterParam) {
        return ResponseEntity.ok(clickUpClient.findFilteredTaskByTeam(id, taskFilterParam));
    }
}
