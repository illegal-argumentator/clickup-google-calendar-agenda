package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder;

import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import org.springframework.web.util.UriComponentsBuilder;

import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths.*;

public class ClickUpPathBuilder {

    public static String buildSpaceByTeamIdPath(String id) {
        return TEAM.getPath() + "/%s/space".formatted(id);
    }

    public static String buildTaskByTeamIdPath(String id) {
        return TEAM.getPath() + "/%s/task".formatted(id);
    }

    public static String buildFolderBySpaceIdPath(String id) {
        return SPACE.getPath() + "/%s/folder".formatted(id);
    }

    public static String buildFolderlessListBySpaceIdPath(String id) {
        return SPACE.getPath() + "/%s/list".formatted(id);
    }

    public static String buildListByFolderIdPath(String id) {
        return FOLDER.getPath() + "/%s/list".formatted(id);
    }

    public static String buildTaskByListIdPath(String id) {
        return LIST.getPath() + "/%s/task".formatted(id);
    }

    public static String buildMembersByListIdPath(String id) {
        return LIST.getPath() + "/%s/member".formatted(id);
    }

    public static String buildTaskByTeamIdPath(String id, TaskFilterParam taskFilterParam) {
        String basePath = buildTaskByTeamIdPath(id);

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

        if (taskFilterParam.getAssignees() != null && !taskFilterParam.getAssignees().isEmpty()) {
            taskFilterParam.getAssignees().forEach(assignee -> uriComponentsBuilder.queryParam("assignees[]", assignee));
        }

        return uriComponentsBuilder.build().toString();
    }
}
