package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import org.springframework.web.util.UriComponentsBuilder;

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

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(basePath);

        if (taskFilterParam.getPage() != null) {
            uriComponentsBuilder.queryParam("page", taskFilterParam.getPage());
        }

        if (taskFilterParam.getOrderBy() != null) {
            uriComponentsBuilder.queryParam("order_by", taskFilterParam.getOrderBy());
        }

        if (taskFilterParam.getReverse() != null) {
            uriComponentsBuilder.queryParam("reverse", taskFilterParam.getReverse());
        }

        if (taskFilterParam.getIncludeClosed() != null) {
            uriComponentsBuilder.queryParam("include_closed", taskFilterParam.getIncludeClosed());
        }

        if (taskFilterParam.getDueDateGt() != null) {
            uriComponentsBuilder.queryParam("due_date_gt", taskFilterParam.getDueDateGt());
        }

        if (taskFilterParam.getDueDateLt() != null) {
            uriComponentsBuilder.queryParam("due_date_lt", taskFilterParam.getDueDateLt());
        }

        if (taskFilterParam.getStatuses() != null && !taskFilterParam.getStatuses().isEmpty()) {
            taskFilterParam.getStatuses().forEach(statusId -> uriComponentsBuilder.queryParam("statuses[]", statusId.trim().replaceAll(" ", "%20")));
        }

        if (taskFilterParam.getListIds() != null && !taskFilterParam.getListIds().isEmpty()) {
            taskFilterParam.getListIds().forEach(listId -> uriComponentsBuilder.queryParam("list_ids[]", listId));
        }

        if (taskFilterParam.getProjectIds() != null && !taskFilterParam.getProjectIds().isEmpty()) {
            taskFilterParam.getProjectIds().forEach(projectId -> uriComponentsBuilder.queryParam("project_ids[]", projectId));
        }

        if (taskFilterParam.getSpaceIds() != null && !taskFilterParam.getSpaceIds().isEmpty()) {
            taskFilterParam.getSpaceIds().forEach(spaceId -> uriComponentsBuilder.queryParam("space_ids[]", spaceId));
        }

        return uriComponentsBuilder.build().toString();
    }
}
