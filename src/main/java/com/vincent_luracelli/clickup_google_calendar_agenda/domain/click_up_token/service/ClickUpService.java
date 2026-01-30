package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ClickUpService {

    private final ClickUpClient clickUpClient;

    public TasksResponse findFilteredTaskByTeam(String id, TaskFilterParam taskFilterParam, User user) {
        TasksResponse tasksResponse = clickUpClient.findFilteredTaskByTeam(id, taskFilterParam, user);
        updateTasksTagByOddLength(tasksResponse.getTasks());
        return tasksResponse;
    }

    public TasksResponse findTasksByList(String id, User user) {
        TasksResponse tasksResponse = clickUpClient.findTasksByList(id, user);
        updateTasksTagByOddLength(tasksResponse.getTasks());
        return tasksResponse;
    }

    public void updateTasksTagByOddLength(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getTags().isEmpty()) continue;

            if (task.getTags().size() > 3 || task.getTags().size() % 2 == 0) {
                task.setTags(List.of());
            }
        }
    }

}
