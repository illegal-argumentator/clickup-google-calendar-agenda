package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.type.SourceType;
import com.vincent_luracelli.clickup_google_calendar_agenda.common.util.OkHttpUtil;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.click_up_token.model.ClickUpToken;
import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.constants.ClickUpPaths;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.*;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service.ClickUpAuthService;
import com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up.dto.TaskFilterParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.builder.ClickUpPathBuilder.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickUpClient {

    private final OkHttpUtil okHttpUtil;

    private final ClickUpAuthService clickUpAuthService;

    public TeamsResponse findTeams(User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = ClickUpPaths.TEAM.getPath();
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TeamsResponse.class);
    }

    @Cacheable("click_up_spaces")
    public SpacesResponse findSpacesByTeam(String id, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildSpaceByTeamIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, SpacesResponse.class);
    }

    @Cacheable("click_up_folders")
    public FoldersResponse findFoldersBySpace(String id, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildFolderBySpaceIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, FoldersResponse.class);
    }

    @Cacheable("click_up_lists")
    public ListsResponse findListsByFolder(String folderId, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildListByFolderIdPath(folderId);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    @Cacheable("click_up_folderless_lists")
    public ListsResponse findFolderlessListsBySpace(String id, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildFolderlessListBySpaceIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, ListsResponse.class);
    }

    public TasksResponse findTasksByList(String id, User userEmail) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(userEmail.getClickUpTokenId());

        String path = buildTaskByListIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TasksResponse.class);
    }

    public TasksResponse findFilteredTaskByTeam(String id, TaskFilterParam taskFilterParam, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildTaskByTeamIdPath(id, taskFilterParam);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, TasksResponse.class);
    }

    @Cacheable("click_up_members_by_list")
    public MembersResponse findMembersByList(String id, User user) {
        ClickUpToken clickUpToken = clickUpAuthService.findClickUpTokenOrThrow(user.getClickUpTokenId());

        String path = buildMembersByListIdPath(id);
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, clickUpToken.getAccessToken())
                .url(path)
                .build();

        return okHttpUtil.handleApiRequest(SourceType.CLICK_UP, request, MembersResponse.class);
    }
}
