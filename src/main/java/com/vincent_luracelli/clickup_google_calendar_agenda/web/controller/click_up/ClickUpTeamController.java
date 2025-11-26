package com.vincent_luracelli.clickup_google_calendar_agenda.web.controller.click_up;

import com.vincent_luracelli.clickup_google_calendar_agenda.domain.user.model.User;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.ClickUpClient;
import com.vincent_luracelli.clickup_google_calendar_agenda.integration.click_up.common.dto.TeamsResponse;
import com.vincent_luracelli.clickup_google_calendar_agenda.security.service.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/click-up")
@RequiredArgsConstructor
public class ClickUpTeamController {

    private final ClickUpClient clickUpClient;

    private final JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/teams")
    ResponseEntity<TeamsResponse> findTeams() {
        User user = jwtUserDetailsService.getUserFromContext();
        return ResponseEntity.ok(clickUpClient.findTeams(user));
    }

}
