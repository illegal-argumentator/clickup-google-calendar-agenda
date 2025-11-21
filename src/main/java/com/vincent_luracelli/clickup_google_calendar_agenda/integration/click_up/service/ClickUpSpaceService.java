package com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service;

import com.vincent_luracelli.clickup_google_calendar_agenda.common.exception.ApiException;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.ListsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.MembersResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.SpacesResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Listing;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.embedded.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ClickUpSpaceService {

    private final ClickUpClient clickUpClient;

    @Cacheable("click_up_members_by_space")
    public MembersResponse findMembersBySpace(String id, String username) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ListsResponse listsBySpace = clickUpClient.findFolderlessListsBySpace(id, username);

        List<Future<Set<Member>>> futures = new ArrayList<>();
        Set<Member> members = new HashSet<>();

        for (Listing listing : listsBySpace.listings()) {
            futures.add(executorService.submit(() -> clickUpClient.findMembersByList(listing.id(), username).getMembers()));
        }

        try {
            for (Future<Set<Member>> future : futures) {
                members.addAll(future.get());
            }
            executorService.shutdown();
        } catch (ExecutionException | InterruptedException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return MembersResponse.builder().members(members).build();
    }

    public SpacesResponse findSpacesByTeam(String id, String username) {
        return clickUpClient.findSpacesByTeam(id, username);
    }
}
