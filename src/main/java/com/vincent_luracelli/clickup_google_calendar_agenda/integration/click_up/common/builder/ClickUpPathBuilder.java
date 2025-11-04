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
            sb.append("&order_by=%s".formatted(taskFilterParam.getOrderBy()));
        }

        if (taskFilterParam.getReverse() != null) {
            sb.append("&reverse=%b".formatted(taskFilterParam.getReverse()));
        }

        if (taskFilterParam.getIncludeClosed() != null) {
            sb.append("&include_closed=%b".formatted(taskFilterParam.getIncludeClosed()));
        }

        if (taskFilterParam.getDueDateGt() != null) {
            sb.append("&due_date_gt=%d".formatted(taskFilterParam.getDueDateGt()));
        }

        if (taskFilterParam.getDueDateLt() != null) {
            sb.append("&due_date_lt=%d".formatted(taskFilterParam.getDueDateLt()));
        }

        if (taskFilterParam.getStatuses() != null && !taskFilterParam.getStatuses().isEmpty()) {
            System.out.println(taskFilterParam.getStatuses());
            taskFilterParam.getStatuses().forEach(statusId -> sb.append("&statuses[]=%s".formatted(statusId.trim().replaceAll(" ", "%20"))));
        }

        if (taskFilterParam.getListIds() != null && !taskFilterParam.getListIds().isEmpty()) {
            taskFilterParam.getListIds().forEach(listId -> sb.append("&list_ids[]=%s".formatted(listId)));
        }

        if (taskFilterParam.getProjectIds() != null && !taskFilterParam.getProjectIds().isEmpty()) {
            taskFilterParam.getProjectIds().forEach(projectId -> sb.append("&project_ids[]=%s".formatted(projectId)));
        }

        if (taskFilterParam.getSpaceIds() != null && !taskFilterParam.getSpaceIds().isEmpty()) {
            taskFilterParam.getSpaceIds().forEach(spaceId -> sb.append("&space_ids[]=%s".formatted(spaceId)));
        }

        System.out.println(sb);

        return sb.toString();
    }

}
