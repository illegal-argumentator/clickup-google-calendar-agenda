package com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.webhook.util.EventUtils;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TasksResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpService {

    private final ClickUpClient clickUpClient;

    public TasksResponse findFilteredTaskByTeam(String id, TaskFilterParam taskFilterParam, User user) {
        TasksResponse tasksResponse = clickUpClient.findFilteredTaskByTeam(id, taskFilterParam, user);
        tasksResponse.setTasks(EventUtils.filterTasksTagByTagName(tasksResponse.getTasks()));
        return tasksResponse;
    }

    public TasksResponse findTasksByList(String id, User user) {
        TasksResponse tasksResponse = clickUpClient.findTasksByList(id, user);
        tasksResponse.setTasks(EventUtils.filterTasksTagByTagName(tasksResponse.getTasks()));
        return tasksResponse;
    }
}
