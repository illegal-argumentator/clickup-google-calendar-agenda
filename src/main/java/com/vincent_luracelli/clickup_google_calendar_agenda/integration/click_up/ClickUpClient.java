package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.*;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder.ClickUpPathBuilder.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpClient {

    @Value("${clickup.api_key}")
    private String CLICKUP_API_KEY;

    private final OkHttpUtil okHttpUtil;

    public TeamsResponse findTeams() {
        String path = ClickUpPaths.TEAM.getPath();
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TeamsResponse.class);
    }

    public SpacesResponse findSpacesByTeam(String teamId) {
        String path = buildSpaceByTeamIdPath(teamId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, SpacesResponse.class);
    }

    public FoldersResponse findFoldersBySpace(String spaceId) {
        String path = buildFolderBySpaceIdPath(spaceId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, FoldersResponse.class);
    }

    public ListsResponse findListsByFolder(String folderId) {
        String path = buildListByFolderIdPath(folderId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    public ListsResponse findFolderlessListsBySpace(String spaceId) {
        String path = buildFolderlessListBySpaceIdPath(spaceId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    public TasksResponse findTasksByList(String listId) {
        String path = buildTaskByListIdPath(listId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TasksResponse.class);
    }

    public TasksResponse findFilteredTaskByTeam(String teamId, TaskFilterParam taskFilterParam) {
        String path = buildTaskByTeamIdPath(teamId, taskFilterParam);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICKUP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TasksResponse.class);
    }
}
