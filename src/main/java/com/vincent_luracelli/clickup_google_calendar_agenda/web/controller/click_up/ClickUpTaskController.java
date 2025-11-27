package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpTaskController {

    private final ClickUpClient clickUpClient;

    @GetMapping("/list/{id}/tasks")
    ResponseEntity<TasksResponse> findTasksByList(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpClient.findTasksByList(id, user));
    }

    @GetMapping("/team/{id}/tasks")
    ResponseEntity<TasksResponse> findFilteredTaskByTeam(
            @PathVariable String id,
            TaskFilterParam taskFilterParam,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpClient.findFilteredTaskByTeam(id, taskFilterParam, user));
    }
}
