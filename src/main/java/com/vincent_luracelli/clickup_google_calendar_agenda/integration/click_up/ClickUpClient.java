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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder.ClickUpPathBuilder.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpClient {

    @Value("${click_up.api_key}")
    private String CLICK_UP_API_KEY;

    private final OkHttpUtil okHttpUtil;

    @Cacheable("click_up_teams")
    public TeamsResponse findTeams() {
        String path = ClickUpPaths.TEAM.getPath();
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TeamsResponse.class);
    }

    @Cacheable("click_up_spaces")
    public SpacesResponse findSpacesByTeam(String id) {
        String path = buildSpaceByTeamIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, SpacesResponse.class);
    }

    @Cacheable("click_up_folders")
    public FoldersResponse findFoldersBySpace(String id) {
        String path = buildFolderBySpaceIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, FoldersResponse.class);
    }

    @Cacheable("click_up_lists")
    public ListsResponse findListsByFolder(String folderId) {
        String path = buildListByFolderIdPath(folderId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    @Cacheable("click_up_folderless_lists")
    public ListsResponse findFolderlessListsBySpace(String id) {
        String path = buildFolderlessListBySpaceIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    public NormalTasksResponse findTasksByList(String id) {
        String path = buildTaskByListIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, NormalTasksResponse.class);
    }

    public FilteredTasksResponse findFilteredTaskByTeam(String id, TaskFilterParam taskFilterParam) {
        String path = buildTaskByTeamIdPath(id, taskFilterParam);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, FilteredTasksResponse.class);
    }

    @Cacheable("click_up_members_by_list")
    public MembersResponse findMembersByList(String id) {
        String path = buildMembersByListIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, CLICK_UP_API_KEY)
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, MembersResponse.class);
    }
}
