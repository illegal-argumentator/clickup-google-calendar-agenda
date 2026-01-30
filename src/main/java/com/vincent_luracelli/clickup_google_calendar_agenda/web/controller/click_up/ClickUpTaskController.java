package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service.ClickUpService;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpTaskController {

    private final ClickUpService clickUpService;

    @GetMapping("/list/{id}/tasks")
    ResponseEntity<TasksResponse> findTasksByList(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpService.findTasksByList(id, user));
    }

    @GetMapping("/team/{id}/tasks")
    ResponseEntity<TasksResponse> findFilteredTaskByTeam(
            @PathVariable String id,
            TaskFilterParam taskFilterParam,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpService.findFilteredTaskByTeam(id, taskFilterParam, user));
    }
}
