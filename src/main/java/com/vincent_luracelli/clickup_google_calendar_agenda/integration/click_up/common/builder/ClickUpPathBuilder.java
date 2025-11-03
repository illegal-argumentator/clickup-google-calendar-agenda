package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths.*;

public class ClickUpPathBuilder {

    public static String buildSpaceByTeamIdPath(String teamId) {
        return TEAM.getPath() + "/%s/space".formatted(teamId);
    }

    public static String buildFolderBySpaceIdPath(String spaceId) {
        return SPACE.getPath() + "/%s/folder".formatted(spaceId);
    }

    public static String buildListByFolderIdPath(String folderId) {
        return FOLDER.getPath() + "/%s/list".formatted(folderId);
    }

    public static String buildFolderlessListBySpaceIdPath(String folderId) {
        return SPACE.getPath() + "/%s/list".formatted(folderId);
    }

    public static String buildTaskByListIdPath(String listId) {
        return LIST.getPath() + "/%s/task".formatted(listId);
    }

    public static String buildTaskByTeamIdPath(String teamId) {
        return TEAM.getPath() + "/%s/task".formatted(teamId);
    }

    public static String buildTaskByTeamIdPath(String teamId, TaskFilterParam taskFilterParam) {
        String basePath = buildTaskByTeamIdPath(teamId);
        StringBuilder sb = new StringBuilder(basePath);

        sb.append("?page=%s".formatted(taskFilterParam.getPage()));

        if (taskFilterParam.getOrderBy() != null) {
            sb.append("&order_by[]=%s".formatted(taskFilterParam.getOrderBy()));
        }

        if (taskFilterParam.getListIds() != null && !taskFilterParam.getListIds().isEmpty()) {
            taskFilterParam.getListIds().forEach(listId -> sb.append("&list_ids[]=%s".formatted(listId)));
        }

        if (taskFilterParam.getProjectIds() != null && !taskFilterParam.getProjectIds().isEmpty()) {
            taskFilterParam.getProjectIds().forEach(listId -> sb.append("&project_ids[]=%s".formatted(listId)));
        }

        if (taskFilterParam.getSpaceIds() != null && !taskFilterParam.getSpaceIds().isEmpty()) {
            taskFilterParam.getSpaceIds().forEach(listId -> sb.append("&space_ids[]=%s".formatted(listId)));
        }

        return sb.toString();
    }

}
