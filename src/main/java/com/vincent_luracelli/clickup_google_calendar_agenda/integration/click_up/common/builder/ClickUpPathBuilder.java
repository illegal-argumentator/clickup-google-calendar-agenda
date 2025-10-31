package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.dto.TaskFilterRequest;

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

    public static String buildTaskByTeamIdPath(String teamId, TaskFilterRequest taskFilterRequest) {
        String basePath = buildTaskByTeamIdPath(teamId);
        StringBuilder sb = new StringBuilder(basePath);

        sb.append("?page=%s".formatted(taskFilterRequest.getPage()));

        if (taskFilterRequest.getOrderBy() != null) {
            sb.append("&order_by[]=%s".formatted(taskFilterRequest.getOrderBy()));
        }

        if (taskFilterRequest.getListIds() != null && !taskFilterRequest.getListIds().isEmpty()) {
            taskFilterRequest.getListIds().forEach(listId -> sb.append("&list_ids[]=%s".formatted(listId)));
        }

        if (taskFilterRequest.getProjectIds() != null && !taskFilterRequest.getProjectIds().isEmpty()) {
            taskFilterRequest.getProjectIds().forEach(listId -> sb.append("&project_ids[]=%s".formatted(listId)));
        }

        if (taskFilterRequest.getSpaceIds() != null && !taskFilterRequest.getSpaceIds().isEmpty()) {
            taskFilterRequest.getSpaceIds().forEach(listId -> sb.append("&space_ids[]=%s".formatted(listId)));
        }

        return sb.toString();
    }

}
