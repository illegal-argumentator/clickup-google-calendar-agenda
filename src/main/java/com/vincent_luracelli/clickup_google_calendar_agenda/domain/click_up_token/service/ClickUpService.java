package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Tag;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Task;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import io.micrometer.common.util.StringUtils;
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
        tasksResponse.setTasks(filterTasksTagByTagName(tasksResponse.getTasks()));
        return tasksResponse;
    }

    public TasksResponse findTasksByList(String id, User user) {
        TasksResponse tasksResponse = clickUpClient.findTasksByList(id, user);
        tasksResponse.setTasks(filterTasksTagByTagName(tasksResponse.getTasks()));
        return tasksResponse;
    }

    public List<Task> filterTasksTagByTagName(List<Task> tasks) {
        String baustrom = "baustrøm", leveringen = "leveringen", bestelbon = "bestelbon";

        return tasks.stream().filter(task -> {
            List<String> tagNames = task.getTags().stream()
                    .map(Tag::name)
                    .filter(StringUtils::isNotBlank)
                    .map(tag -> tag.trim().toLowerCase())
                    .toList();
            return !tagNames.contains(leveringen) && (tagNames.contains(baustrom) || tagNames.contains(bestelbon));
        }).toList();
    }

}
