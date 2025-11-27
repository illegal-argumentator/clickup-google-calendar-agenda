package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.SpacesResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.MembersResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service.ClickUpSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpSpaceController {

    private final ClickUpSpaceService clickUpSpaceService;

    @GetMapping("/team/{id}/spaces")
    ResponseEntity<SpacesResponse> findSpacesByTeam(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpSpaceService.findSpacesByTeam(id, user));
    }

    @GetMapping("/member/all/space/{id}")
    ResponseEntity<MembersResponse> findMembersBySpace(@PathVariable String id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clickUpSpaceService.findMembersBySpace(id, user));
    }
}
