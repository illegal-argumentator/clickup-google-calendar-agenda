package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/search/")
    public void searchTasks() {
        
    }

}
