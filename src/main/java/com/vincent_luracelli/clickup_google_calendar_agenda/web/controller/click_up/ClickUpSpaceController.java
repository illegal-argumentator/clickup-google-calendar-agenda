package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.SpacesResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.MembersResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.service.ClickUpSpaceService;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpSpaceController {

    private final ClickUpSpaceService clickUpSpaceService;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/team/{id}/spaces")
    ResponseEntity<SpacesResponse> findSpacesByTeam(@PathVariable String id) {
        String username = jwtUserDetailsService.getUserFromContext().getUsername();
        return ResponseEntity.ok(clickUpSpaceService.findSpacesByTeam(id, username));
    }

    @GetMapping("/member/all/space/{id}")
    ResponseEntity<MembersResponse> findMembersBySpace(@PathVariable String id) {
        String username = jwtUserDetailsService.getUserFromContext().getUsername();
        return ResponseEntity.ok(clickUpSpaceService.findMembersBySpace(id, username));
    }
}
